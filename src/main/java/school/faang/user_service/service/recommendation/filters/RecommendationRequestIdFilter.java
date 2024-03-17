package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Component
public class RecommendationRequestIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getId() != null;
    }

    @Override
    public void apply(List<RecommendationRequest> recommendationRequests, RequestFilterDto filters) {
        recommendationRequests.removeIf(recommendationRequest -> recommendationRequest.getId() != filters.getId());
    }
}
