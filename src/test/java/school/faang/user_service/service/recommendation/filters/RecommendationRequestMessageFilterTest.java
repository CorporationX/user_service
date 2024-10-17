package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.filter.recommendation.RecommendationRequestMessageFilter;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.entity.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestMessageFilterTest {
  private RecommendationRequestMessageFilter recommendationRequestMessageFilter = new RecommendationRequestMessageFilter();
    RecommendationRequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setMessagePattern("testMessage");
        assertTrue(recommendationRequestMessageFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestMessageFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setMessagePattern("Mess");
        RecommendationRequest firstRecommendationRequest = new RecommendationRequest();
        firstRecommendationRequest.setMessage("testMessage");
        RecommendationRequest secondRecommendationRequest = new RecommendationRequest();
        secondRecommendationRequest.setMessage("some text");
        RecommendationRequest thirdRecommendationRequest = new RecommendationRequest();
        thirdRecommendationRequest.setMessage("some Message");
        Stream<RecommendationRequest> recommendationRequests = Stream.of(firstRecommendationRequest, secondRecommendationRequest, thirdRecommendationRequest);
        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestMessageFilter.apply(recommendationRequests, requestFilterDto).toList();

        assertAll(
                () -> assertEquals(resultRecommendationRequests.get(0).getMessage(), firstRecommendationRequest.getMessage()),
                () -> assertEquals(resultRecommendationRequests.get(1).getMessage(), thirdRecommendationRequest.getMessage()),
                () -> assertEquals(2, resultRecommendationRequests.size())
        );
    }
}