package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import java.time.LocalDateTime;
import java.util.List;

public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void testRecommendationRequestStatusFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.REJECTED);
        recommendationRequest2.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest3.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().status(RequestStatus.REJECTED).build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        Assertions.assertEquals(2, requestsByFilter.size());
        Assertions.assertEquals(RequestStatus.REJECTED, requestsByFilter.get(0).getStatus());
        Assertions.assertEquals(RequestStatus.REJECTED, requestsByFilter.get(1).getStatus());
    }

    @Test
    void testRecommendationRequestRequesterIdFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setRequester(User.builder().id(1L).build());
        recommendationRequest2.setRequester(User.builder().id(2L).build());
        recommendationRequest3.setRequester(User.builder().id(1L).build());

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().receiverId(1L).build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        Assertions.assertEquals(2, requestsByFilter.size());
        Assertions.assertEquals(1L, requestsByFilter.get(0).getReceiverId());
        Assertions.assertEquals(1L, requestsByFilter.get(1).getReceiverId());
    }

    @Test
    void testGetRequestsWithManyFilters() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest1.setRequester(User.builder().id(1L).build());
        recommendationRequest1.setReceiver(User.builder().id(2L).build());
        recommendationRequest1.setCreatedAt(LocalDateTime.now().minusMonths(2));

        recommendationRequest2.setStatus(RequestStatus.REJECTED);
        recommendationRequest2.setRequester(User.builder().id(2L).build());
        recommendationRequest2.setReceiver(User.builder().id(1L).build());
        recommendationRequest2.setCreatedAt(LocalDateTime.now().minusMonths(1));

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .status(RequestStatus.ACCEPTED)
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now().minusMonths(2))
                .build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        List<RecommendationRequestDto> expectedResult = requests.stream().map(recommendationRequestMapper::toDto).toList();

        Assertions.assertEquals(expectedResult, requestsByFilter);
    }
}
