package school.faang.user_service.dto.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
class RecommendationRequestIdFilter implements RecommendationRequestFilterInterface {
    @Override
    public boolean isApplicable(RecommendationRequestFilter filter) {
        return filter.getId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requestStream, RecommendationRequestFilter filter
    ) {
        return requestStream.filter(recommendationRequest -> recommendationRequest.getId() == filter.getId());
    }
}
