package school.faang.user_service.dto.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
class RecommendationRequestCreatedAtFilter implements RecommendationRequestFilterInterface {
    @Override
    public boolean isApplicable(RecommendationRequestFilter filter) {
        return filter.getCreatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requestStream, RecommendationRequestFilter filter
    ) {
        return requestStream.filter(
                recommendationRequest -> recommendationRequest.getCreatedAt().isAfter(filter.getCreatedAt())
        );
    }
}
