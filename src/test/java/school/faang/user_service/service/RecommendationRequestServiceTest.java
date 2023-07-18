package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.requestfilter.CreateAtFilter;
import school.faang.user_service.filter.requestfilter.IdFilter;
import school.faang.user_service.filter.requestfilter.MessageFilter;
import school.faang.user_service.filter.requestfilter.ReceiverIdFilter;
import school.faang.user_service.filter.requestfilter.RequestFilter;
import school.faang.user_service.filter.requestfilter.RequestStatusFilter;
import school.faang.user_service.filter.requestfilter.SkillRequestFilter;
import school.faang.user_service.filter.requestfilter.UpdateAtFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    private RecommendationRequest requestEntity;

    private RecommendationRequestDto requestDto1;

    private RecommendationRequestDto requestDto2;

    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(7);
        requestDto1 = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(new SkillRequest(1L, new RecommendationRequest(), Skill.builder().id(1).build())))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(createdAt)
                .build();
        requestDto2 = RecommendationRequestDto.builder()
                .id(2L)
                .build();
        requestFilterDto = RequestFilterDto.builder()
                .id(1L)
                .message("ssa")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(new SkillRequest(1L, new RecommendationRequest(), Skill.builder().id(1).build())))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(createdAt)
                .build();

        requestEntity = recommendationRequestMapper.toEntity(requestDto1);
    }

    @Test
    void testValidationExistByIdThrowNegative() {
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationExistByIdThrowPositive() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationRequestDateNegative() {
        requestDto1.setCreatedAt(LocalDateTime.now().minusMonths(5));
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationRequestDatePositive() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationExistSkillNegative() {
        requestDto1.setCreatedAt(LocalDateTime.now());
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationExistSkillPositive() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testGetRequests() {
        RecommendationRequest entity1 = recommendationRequestMapper.toEntity(requestDto1);
        RecommendationRequest entity2 = recommendationRequestMapper.toEntity(requestDto2);

        List<RequestFilter> filters = List.of(
                new IdFilter(),
                new MessageFilter(),
                new SkillRequestFilter(),
                new ReceiverIdFilter(),
                new RequestStatusFilter(),
                new CreateAtFilter(),
                new UpdateAtFilter()
        );

        recommendationRequestService.setRequestFilters(filters);

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        List<RecommendationRequestDto> expected = List.of(requestDto1);

        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsIdNegative() {
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.getRequestsId(1L));
    }

    @Test
    void testGetRequestsId() {
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(requestEntity));

        RecommendationRequestDto actual = recommendationRequestService.getRequestsId(1L);

        assertEquals(requestDto1, actual);
    }

    @Test
    void testRejectRequest() {
        RejectionDto rejectionDto = RejectionDto.builder().reason("not enough skills").build();
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(requestEntity));

        requestEntity.setStatus(RequestStatus.PENDING);
        requestEntity.setRejectionReason(rejectionDto.getReason());

        recommendationRequestService.rejectRequest(1L, rejectionDto);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(requestEntity);
    }

    @Test
    void testRejectRequestNegative() {
        assertThrows(DataValidationException.class, () -> recommendationRequestService.rejectRequest(1L, RejectionDto.builder().build()));
    }
}