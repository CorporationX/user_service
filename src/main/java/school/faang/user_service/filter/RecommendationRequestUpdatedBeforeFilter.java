package school.faang.user_service.filter;

import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestUpdatedBeforeFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestRequestFilterDto) {
        return requestRequestFilterDto.getUpdatedBefore() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                               RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequests.filter(recommendationRequest ->
                recommendationRequest.getUpdatedAt().isBefore(recommendationRequestFilterDto.getUpdatedBefore()));
    }
}
