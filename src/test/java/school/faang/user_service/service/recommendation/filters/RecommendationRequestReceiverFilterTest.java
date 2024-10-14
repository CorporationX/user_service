package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.filter.recommendation.RecommendationRequestReceiverFilter;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestReceiverFilterTest {
    RecommendationRequestReceiverFilter recommendationRequestReceiverFilter = new RecommendationRequestReceiverFilter();
    RecommendationRequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setReceiverIdPattern(11L);
        assertTrue(recommendationRequestReceiverFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestReceiverFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setReceiverIdPattern(11L);
        RecommendationRequest firstRecommendationRequest = new RecommendationRequest();
        firstRecommendationRequest.setReceiver(new User() {{
            setId(1L);
        }});
        RecommendationRequest secondRecommendationRequest = new RecommendationRequest();
        secondRecommendationRequest.setReceiver(new User() {{
            setId(11L);
        }});
        RecommendationRequest thirdRecommendationRequest = new RecommendationRequest();
        thirdRecommendationRequest.setReceiver(new User() {{
            setId(11L);
        }});
        Stream<RecommendationRequest> recommendationRequests = Stream.of(firstRecommendationRequest, secondRecommendationRequest, thirdRecommendationRequest);

        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestReceiverFilter.apply(recommendationRequests, requestFilterDto).toList();

        assertAll(
                () -> assertEquals(resultRecommendationRequests.get(0).getMessage(), secondRecommendationRequest.getMessage()),
                () -> assertEquals(resultRecommendationRequests.get(1).getMessage(), thirdRecommendationRequest.getMessage()),
                () -> assertEquals(2, resultRecommendationRequests.size())
        );
    }
}