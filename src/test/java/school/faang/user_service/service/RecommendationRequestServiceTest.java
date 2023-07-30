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
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.requestfilter.CreateAtFilter;
import school.faang.user_service.filter.requestfilter.ReceiverIdFilter;
import school.faang.user_service.filter.requestfilter.RequestFilter;
import school.faang.user_service.filter.requestfilter.RequestStatusFilter;
import school.faang.user_service.filter.requestfilter.SkillRequestFilter;
import school.faang.user_service.filter.requestfilter.UpdateAtFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private SkillValidator skillValidator;

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
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skillsId(List.of(1L))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
        requestDto2 = RecommendationRequestDto.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .requesterId(2L)
                .receiverId(2L)
                .skillsId(List.of(2L))
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
        entity1 = RecommendationRequest.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(SkillRequest.builder().id(1L).build()))
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(1L).build())
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
        entity2 = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.REJECTED)
                .requester(User.builder().id(2L).build())
                .receiver(User.builder().id(2L).build())
                .skills(List.of(SkillRequest.builder().id(2L).build()))
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
        recommendationRequestService.create(requestDto1);
        Mockito.verify(recommendationRequestRepository).save(recommendationRequestMapper.toEntity(requestDto1));
    }

    @Test
    void testGetRequestsWithAllFilters() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .statusPattern(RequestStatus.ACCEPTED)
                .skillsPattern(List.of(1L))
                .requesterIdPattern(1L)
                .receiverIdPattern(1L)
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
                .receiverIdPattern(1L)
                .build();

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        List<RecommendationRequestDto> expected = List.of(requestDto1);

        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(expected, actual);
    }

    @Test
    void testGetRequestsWithRequesterIdFilter() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .receiverIdPattern(1L)
                .build();

        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        List<RecommendationRequestDto> expected = List.of(requestDto1);

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
}