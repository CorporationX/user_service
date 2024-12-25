package school.faang.user_service.filter.recommendationRequestFilters;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class StatusRecommendationRequestFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.status() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(request -> request.getStatus().equals(filterDto.status()));
    }
}
