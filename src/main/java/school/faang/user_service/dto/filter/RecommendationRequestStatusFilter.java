package school.faang.user_service.dto.filter;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestStatusFilter implements RecommendationRequestFilterInterface {
    @Override
    public boolean isApplicable(RecommendationRequestFilter filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requestStream, RecommendationRequestFilter filter
    ) {
        return requestStream.filter(
                recommendationRequest -> recommendationRequest.getStatus().equals(filter.getStatus())
        );
    }
}
