package school.faang.user_service.filter.recommendationRequestFilters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationIdRecommendationRequestFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.requestId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(recommendationRequest -> recommendationRequest.getId() == filterDto.requestId());
    }
}
