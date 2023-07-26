package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestUpdatedAfterFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestRequestFilterDto) {
        return requestRequestFilterDto.getUpdatedAfter() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                               RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequests.filter(recommendationRequest ->
                recommendationRequest.getUpdatedAt().isAfter(recommendationRequestFilterDto.getUpdatedAfter()));
    }
}
