package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class MessagePatternFilter implements RecommendationFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.messagePattern() != null && !filters.messagePattern().isBlank();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> request.getMessage().contains(filters.messagePattern()));
    }
}