package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void shouldThrowExceptionWhenRequesterNotFound() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        assertEquals("Requester not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReceiverNotFound() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        assertEquals("Receiver not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRecommendationRequestExistsInLast6Months() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(5));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(existingRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        assertEquals("You can request a recommendation only once every 6 months", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSkillNotFound() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setSkillIds(List.of(101L, 102L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(skillRepository.findAllByIdIn(List.of(101L, 102L)))
                .thenReturn(List.of(new Skill()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        assertEquals("One or more skills not found", exception.getMessage());
    }

    @Test
    void shouldSaveSkills() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        List<Long> skillIds = List.of(101L, 102L);

        Skill skill1 = new Skill();
        skill1.setId(101L);
        Skill skill2 = new Skill();
        skill2.setId(102L);

        when(skillRepository.findAllByIdIn(skillIds)).thenReturn(List.of(skill1, skill2));

        List<SkillRequest> savedSkills = recommendationRequestService.saveSkills(skillIds, recommendationRequest);

        assertNotNull(savedSkills);
        assertEquals(2, savedSkills.size());
        assertEquals(skill1, savedSkills.get(0).getSkill());
        assertEquals(skill2, savedSkills.get(1).getSkill());
        assertEquals(recommendationRequest, savedSkills.get(0).getRequest());
        assertEquals(recommendationRequest, savedSkills.get(1).getRequest());

        verify(skillRepository, times(1)).findAllByIdIn(skillIds);
        verify(skillRequestRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldReturnCreatedRecommendationRequest() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setSkillIds(List.of(101L, 102L));

        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(requestDto);
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setSkills(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        when(skillRepository.findAllByIdIn(List.of(101L, 102L)))
                .thenReturn(List.of(new Skill(), new Skill()));

        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest);

        RecommendationRequestDto result = recommendationRequestService.create(requestDto);

        assertNotNull(result);
        assertEquals(requestDto.getRequesterId(), result.getRequesterId());
        assertEquals(requestDto.getReceiverId(), result.getReceiverId());
        assertEquals("PENDING", result.getStatus().getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRequestNotFound() {
        Long requestId = 1L;
        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.getRequest(requestId);
        });
        assertEquals("Recommendation request with id 1 not found", exception.getMessage());
    }

    @Test
    void shouldReturnExistingRequest() {
        Long requestId = 1L;
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(requestId);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequestDto result = recommendationRequestService.getRequest(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void testRejectRequestWhenRequestNotFound() {
        Long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.rejectRequest(requestId, rejectionDto)
        );
    }

    @Test
    void testRejectRequestWhenRequestAlreadyProcessed() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(requestId);
        recommendationRequest.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(recommendationRequest));

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.rejectRequest(requestId, rejectionDto)
        );
    }

    @Test
    void shouldReturnRejectedRequest() {
        Long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(requestId);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest);

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(requestId, rejectionDto);

        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus().getStatus());
        assertEquals(requestId, result.getId());
    }
}
