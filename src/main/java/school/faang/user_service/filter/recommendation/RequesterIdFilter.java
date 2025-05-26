package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RequesterIdFilter implements RecommendationFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.requesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> request.getRequester().getId().equals(filters.requesterId()));
    }
}