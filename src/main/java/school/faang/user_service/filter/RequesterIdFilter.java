package school.faang.user_service.filter;

import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.RequestFilter;

public class RequesterIdFilter implements RequestFilter {
    private final Long requesterId;

    public RequesterIdFilter(Long requesterId) {
        this.requesterId = requesterId;
    }

    @Override
    public boolean apply(RecommendationRequest request) {
        return requesterId == null || request.getRequester().getId().equals(requesterId);
    }
}
