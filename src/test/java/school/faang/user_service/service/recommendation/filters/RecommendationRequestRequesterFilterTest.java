package school.faang.user_service.service.recommendation.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.filter.recommendation.RecommendationRequestRequesterFilter;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestRequesterFilterTest {
    RecommendationRequestRequesterFilter recommendationRequestRequesterFilter = new RecommendationRequestRequesterFilter();
    RecommendationRequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setRequestIdPattern(11L);
        assertTrue(recommendationRequestRequesterFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestRequesterFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setRequestIdPattern(11L);
        RecommendationRequest firstRecommendationRequest = new RecommendationRequest();
        firstRecommendationRequest.setRequester(new User() {{
            setId(1L);
        }});
        RecommendationRequest secondRecommendationRequest = new RecommendationRequest();
        secondRecommendationRequest.setRequester(new User() {{
            setId(11L);
        }});
        RecommendationRequest thirdRecommendationRequest = new RecommendationRequest();
        thirdRecommendationRequest.setRequester(new User() {{
            setId(11L);
        }});
        Stream<RecommendationRequest> recommendationRequests = Stream.of(firstRecommendationRequest, secondRecommendationRequest, thirdRecommendationRequest);

        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestRequesterFilter.apply(recommendationRequests, requestFilterDto).toList();

        assertAll(
                () -> assertEquals(resultRecommendationRequests.get(0).getMessage(), secondRecommendationRequest.getMessage()),
                () -> assertEquals(resultRecommendationRequests.get(1).getMessage(), thirdRecommendationRequest.getMessage()),
                () -> assertEquals(2, resultRecommendationRequests.size())
        );
    }
}