package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestDtoRequesterFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filters) {
        return filters.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                               RecommendationRequestFilterDto filter) {
        return recommendationRequests.filter(recommendationRequest ->
                recommendationRequest.getRequester().getId().equals(filter.getRequesterId()));
    }
}
