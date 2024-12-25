package school.faang.user_service.filter.recommedationRequestFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.RequestStatus;
import school.faang.user_service.model.User;
import school.faang.user_service.model.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendationRequestFilters.*;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationRequestFilterTest {

    private RecommendationRequestFilter filter;
    private RecommendationRequest firstRequest;
    private RecommendationRequest secondRequest;
    private RequestFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        firstRequest = new RecommendationRequest();
        secondRequest = new RecommendationRequest();
    }

    @Test
    public void testReceiverIdRecommendationRequestFilter() {
        filter = new ReceiverIdRecommendationRequestFilter();
        User receiver = new User();
        receiver.setId(1L);
        firstRequest.setReceiver(receiver);
        User secondReceiver = new User();
        secondReceiver.setId(2L);
        secondRequest.setReceiver(secondReceiver);
        filterDto = RequestFilterDto.builder()
                .receiverId(1L)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<RecommendationRequest> stream = filter.apply(Stream.of(firstRequest, secondRequest), filterDto);
        assertTrue(stream.allMatch(req -> Objects.equals(req.getReceiver().getId(), filterDto.receiverId())));
    }

    @Test
    public void testStatusRecommendationRequestFilter() {
        filter = new StatusRecommendationRequestFilter();
        firstRequest.setStatus(RequestStatus.ACCEPTED);
        secondRequest.setStatus(RequestStatus.REJECTED);
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .status(RequestStatus.ACCEPTED)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<RecommendationRequest> stream = filter.apply(Stream.of(firstRequest, secondRequest), filterDto);
        assertTrue(stream.allMatch(req -> Objects.equals(req.getStatus(), filterDto.status())));
    }

    @Test
    public void testRecommendationIdRequestFilter() {
        filter = new RecommendationIdRecommendationRequestFilter();
        firstRequest.setId(1L);
        secondRequest.setId(2L);
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .requestId(1L)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        Stream<RecommendationRequest> stream = filter.apply(Stream.of(firstRequest, secondRequest), filterDto);
        assertTrue(stream.allMatch(req -> Objects.equals(req.getId(), filterDto.requestId())));
    }

    @Test
    public void testRequesterIdRecommendationRequestFilter() {
        filter = new RequesterIdRecommendationRequestFilter();
        RecommendationRequestFilter falseFilter = new RecommendationIdRecommendationRequestFilter();
        User firstRequester = new User();
        firstRequester.setId(1L);
        firstRequest.setRequester(firstRequester);
        User secondRequester = new User();
        secondRequester.setId(2L);
        secondRequest.setRequester(secondRequester);
        filterDto = RequestFilterDto.builder()
                .requesterId(1L)
                .build();

        assertTrue(filter.isApplicable(filterDto));
        assertFalse(falseFilter.isApplicable(filterDto));
        Stream<RecommendationRequest> stream = filter.apply(Stream.of(firstRequest, secondRequest), filterDto);
        assertTrue(stream.allMatch(req -> Objects.equals(req.getRequester().getId(), filterDto.requesterId())));
    }
}
