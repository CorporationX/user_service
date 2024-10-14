package school.faang.user_service.filter;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.RequestFilter;

public class StatusFilter implements RequestFilter {
    private final RequestStatus status;

    public StatusFilter(RequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean apply(RecommendationRequest request) {
        return status == null || request.getStatus().equals(status);
    }
}
