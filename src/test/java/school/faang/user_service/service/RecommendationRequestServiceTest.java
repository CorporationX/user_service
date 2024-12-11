package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.filter.recommendationRequestFilters.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private UserService userService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillRequestService skillRequestService;
    @Mock
    private List<RecommendationRequestFilter> recommendationRequestFilters;

    private Long requesterId;
    private Long receiverId;
    private Long skillId;
    private User requester;
    private User receiver;
    private Skill skill;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private SkillRequest skillRequest;
    private List<RecommendationRequest> recommendationRequests;

    @BeforeEach
    public void setup() {
        skillId = 1L;
        skillRequest = new SkillRequest();
        requesterId = 1L;
        receiverId = 2L;
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(RequestStatus.PENDING)
                .message("any string")
                .createdAt(LocalDateTime.now())
                .skillIds(List.of(1L, 2L))
                .build();
        requester = new User();
        requester.setId(1L);
        receiver = new User();
        receiver.setId(2L);
        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setSkills(List.of(skillRequest));
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setUpdatedAt(LocalDateTime.MAX);
        skill = new Skill();
        skill.setId(1L);
    }

    @Test
    void testCreateRecommendationRequest() {
        when(recommendationRequestMapper.toDto(any())).thenReturn(recommendationRequestDto);
        when(recommendationRequestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any())).thenReturn(recommendationRequest);
        when(userService.getUserById(requesterId)).thenReturn(requester);
        when(userService.getUserById(receiverId)).thenReturn(receiver);
        when(skillService.getSkillById(skillId)).thenReturn(skill);
        when(skillRequestService.save(any())).thenReturn(skillRequest);

        RecommendationRequestDto requestDto = recommendationRequestService.create(recommendationRequestDto);

        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestMapper, times(1)).toEntity(any());
        verify(recommendationRequestMapper, times(1)).toDto(any());
        verify(userService, times(1)).getUserById(requesterId);
        verify(userService, times(1)).getUserById(receiverId);
        verify(skillService, times(1)).getSkillById(skillId);
        verify(skillRequestService, times(1)).save(skillRequest);
        assertEquals(requestDto.id(), recommendationRequestDto.id());
    }

    @Test
    public void testCreateRecommendationRequestWithNotValidParameters() {
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        when(recommendationRequestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequest.getRequester().getId(),
                recommendationRequest.getReceiver().getId())).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(userService.getUserById(requesterId)).thenReturn(requester);
        when(userService.getUserById(receiverId)).thenReturn(receiver);
        when(skillService.getSkillById(skillId)).thenReturn(skill);

        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testGetRecommendationRequest() {
        when(recommendationRequestRepository.findById(recommendationRequestDto.id()))
                .thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(any())).thenReturn(recommendationRequestDto);

        recommendationRequestService.getRequest(recommendationRequestDto.id());

        verify(recommendationRequestRepository, times(1)).findById(recommendationRequestDto.id());
        verify(recommendationRequestMapper, times(1)).toDto(any());
    }

    @Test
    public void testGetNotExistsRecommendationRequest() {
        assertThrows(ResourceNotFoundException.class, () -> recommendationRequestService.getRequest(anyLong()));
    }

    @Test
    public void testRejectRecommendationRequest() {
        when(recommendationRequestMapper.toDto(any())).thenReturn(recommendationRequestDto);
        when(recommendationRequestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any())).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any())).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findById(recommendationRequestDto.id()))
                .thenReturn(Optional.of(recommendationRequest));
        RecommendationRejectionDto rejectionDto = RecommendationRejectionDto.builder()
                .recommendationId(recommendationRequest.getId())
                .reason(anyString())
                .build();

        RecommendationRequestDto requestDto = recommendationRequestService.rejectRequest(rejectionDto);

        verify(recommendationRequestRepository, times(1)).findById(recommendationRequest.getId());
        verify(recommendationRequestMapper, times(1)).toEntity(any());
        verify(recommendationRequestMapper, times(2)).toDto(any());
        verify(recommendationRequestRepository, times(1)).save(any());
        verify(recommendationRequestRepository, times(1)).findById(recommendationRequest.getId());
        assertEquals(requestDto.id(), rejectionDto.recommendationId());
    }

    @Test
    public void testRejectTheAcceptedRecommendationRequest() {
        when(recommendationRequestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findById(recommendationRequestDto.id()))
                .thenReturn(Optional.of(recommendationRequest));
        RecommendationRejectionDto rejectionDto = RecommendationRejectionDto.builder()
                .recommendationId(recommendationRequest.getId())
                .reason(anyString())
                .build();
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);

        assertThrows(DataValidationException.class, () -> recommendationRequestService.rejectRequest(rejectionDto));
    }

    @Test
    public void testGetRecommendationRequests() {
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .receiverId(1L)
                .build();
        RecommendationRequest firstRequest = new RecommendationRequest();
        firstRequest.setId(1L);
        RecommendationRequest secondRequest = new RecommendationRequest();
        secondRequest.setId(2L);
        RecommendationRequestDto firstDto = RecommendationRequestDto.builder()
                .id(1L)
                .build();
        RecommendationRequestDto secondDto = RecommendationRequestDto.builder()
                .id(2L)
                .build();
        List<RecommendationRequestDto> dtoList = List.of(firstDto, secondDto);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(firstRequest, secondRequest));
        when(recommendationRequestMapper.toDtoList(anyList())).thenReturn(dtoList);

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(recommendationRequestMapper, times(1)).toDtoList(anyList());
        assertEquals(dtoList.size(), result.size());
    }


}
