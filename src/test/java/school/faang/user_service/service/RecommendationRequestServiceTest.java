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
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.requestfilter.CreateAtFilter;
import school.faang.user_service.filter.requestfilter.ReceiverIdFilter;
import school.faang.user_service.filter.requestfilter.RequestFilter;
import school.faang.user_service.filter.requestfilter.RequestStatusFilter;
import school.faang.user_service.filter.requestfilter.SkillRequestFilter;
import school.faang.user_service.filter.requestfilter.UpdateAtFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;
    private RecommendationRequestDto requestDto1;
    private RecommendationRequestDto requestDto2;
    private LocalDateTime createdAt;
    RecommendationRequest entity1;
    RecommendationRequest entity2;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now().minusMonths(7);

        requestDto1 = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .receiverId(2L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skillsId(List.of(1L))
                .recommendationId(1L)
                .build();

        requestDto2 = RecommendationRequestDto.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .requesterId(1L)
                .receiverId(2L)
                .skillsId(List.of(2L))
                .build();

        entity1 = RecommendationRequest.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(SkillRequest.builder()
                        .id(1L)
                        .skill(Skill.builder()
                                .id(1L)
                                .build())
                        .build()))
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .recommendation(Recommendation.builder().id(1L).build())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        entity2 = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .skills(List.of(SkillRequest.builder()
                        .id(2L)
                        .skill(Skill.builder()
                                .id(2L)
                                .build())
                        .build()))
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
        List<RequestFilter> filters = List.of(
                new SkillRequestFilter(),
                new ReceiverIdFilter(),
                new RequestStatusFilter(),
                new CreateAtFilter(),
                new UpdateAtFilter()
        );

        recommendationRequestService.setRequestFilters(filters);
    }

    @Test
    void tesCreate() {
        RecommendationRequest oldRequest1 = RecommendationRequest.builder()
                .message("request1")
                .receiver(User.builder().id(2L).build())
                .requester(User.builder().id(1L).build())
                .createdAt(LocalDateTime.now().minusMonths(8))
                .build();

        RecommendationRequest oldRequest2 = RecommendationRequest.builder()
                .message("request2")
                .receiver(User.builder().id(2L).build())
                .requester(User.builder().id(1L).build())
                .createdAt(LocalDateTime.now().minusMonths(5))
                .build();

        RecommendationRequest oldRequest3 = RecommendationRequest.builder()
                .message("request3")
                .receiver(User.builder().id(2L).build())
                .requester(User.builder().id(1L).build())
                .createdAt(LocalDateTime.now().minusMonths(11))
                .build();

        RecommendationRequest entity = recommendationRequestMapper.toEntity(requestDto1);

        SkillRequest skillRequest = SkillRequest.builder().request(entity1).skill(Skill.builder().id(1L).build()).build();

        entity1.setCreatedAt(null);
        entity1.setUpdatedAt(null);
        entity1.setSkills(null);

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(oldRequest1, oldRequest2, oldRequest3));
        Mockito.when(recommendationRequestRepository.save(entity)).thenReturn(entity1);

        recommendationRequestService.create(requestDto1);

        Mockito.verify(recommendationRequestRepository).save(entity1);
        Mockito.verify(skillRequestRepository).save(skillRequest);
    }

    @Test
    void testGetRequestsWithAllFilters() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .statusPattern(RequestStatus.ACCEPTED)
                .skillsPattern(List.of(1L))
                .requesterIdPattern(1L)
                .receiverIdPattern(2L)
                .createdAtPattern(createdAt)
                .build();

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));
        List<RecommendationRequestDto> expected = List.of(requestDto1);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithCreateAtFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .createdAtPattern(createdAt)
                .build();
        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));
        List<RecommendationRequestDto> expected = List.of(requestDto1, requestDto2);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithReceiverIdFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .receiverIdPattern(2L)
                .build();

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        List<RecommendationRequestDto> expected = List.of(requestDto1, requestDto2);

        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithRequesterIdFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .requesterIdPattern(1L)
                .build();

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        List<RecommendationRequestDto> expected = List.of(requestDto1, requestDto2);

        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithRequestStatusFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .statusPattern(RequestStatus.ACCEPTED)
                .build();
        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));
        List<RecommendationRequestDto> expected = List.of(requestDto1);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithSkillRequestFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .skillsPattern(List.of(1L))
                .build();
        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));
        List<RecommendationRequestDto> expected = List.of(requestDto1);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithUpdateAtFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .updatedAtPattern(createdAt)
                .build();
        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));
        List<RecommendationRequestDto> expected = List.of(requestDto1, requestDto2);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsIdThrowsEntityNotFoundException() {
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.getRecommendationRequest(1L));
    }

    @Test
    void testGetRecommendationRequest() {
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(entity1));
        RecommendationRequestDto actual = recommendationRequestService.getRecommendationRequest(1L);
        assertEquals(requestDto1, actual);
    }

    @Test
    void testRejectRequest() {
        RejectionDto rejectionDto = RejectionDto.builder().reason("not enough skills").build();
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(entity1));
        entity1.setStatus(RequestStatus.PENDING);
        entity1.setRejectionReason(rejectionDto.getReason());
        requestDto1.setRejectionReason(rejectionDto.getReason());
        requestDto1.setStatus(RequestStatus.REJECTED);
        RecommendationRequestDto actual = recommendationRequestService.rejectRequest(1L, rejectionDto);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(entity1);
        assertEquals(requestDto1, actual);
    }

    @Test
    void testRejectRequestThrowsEntityNotFoundException() {
        RejectionDto rejectionDto = RejectionDto.builder().build();
        assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestService.rejectRequest(1L, rejectionDto)
        );
    }
}