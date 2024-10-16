package school.faang.user_service.service.recommendation.filters;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.filter.recommendation.RecommendationRequestStatusFilter;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.entity.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestStatusFilterTest {
    RecommendationRequestStatusFilter recommendationRequestStatusFilter = new RecommendationRequestStatusFilter();
    RecommendationRequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RecommendationRequestFilterDto();
    }

    @Test
    void testIsApplicableTrue() {
        requestFilterDto.setStatusPattern(RequestStatus.PENDING);
        assertTrue(recommendationRequestStatusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableFalse() {
        assertFalse(recommendationRequestStatusFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApply() {
        requestFilterDto.setStatusPattern(RequestStatus.PENDING);
        Stream<RecommendationRequest> recommendationRequests = getRecommendationRequestStream();

        List<RecommendationRequest> resultRecommendationRequests = recommendationRequestStatusFilter.apply(recommendationRequests, requestFilterDto).toList();

        Assertions.assertAll(
                () -> assertEquals(resultRecommendationRequests.get(0).getStatus(), requestFilterDto.getStatusPattern()),
                () -> assertEquals(1, resultRecommendationRequests.size())
        );
    }

    private static @NotNull Stream<RecommendationRequest> getRecommendationRequestStream() {
        RecommendationRequest firstRecommendationRequest = new RecommendationRequest() {{
            setStatus(RequestStatus.ACCEPTED);
        }};
        RecommendationRequest secondRecommendationRequest = new RecommendationRequest() {{
            setStatus(RequestStatus.REJECTED);
        }};
        RecommendationRequest thirdRecommendationRequest = new RecommendationRequest() {{
            setStatus(RequestStatus.PENDING);
        }};
        return Stream.of(firstRecommendationRequest, secondRecommendationRequest, thirdRecommendationRequest);
    }
}