package school.faang.user_service.util.recommendation.filters;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

public interface RecommendationRequestFilter {

    boolean isApplicable(RequestFilterDto filters);

    void apply(List<RecommendationRequest> recommendationRequests, RequestFilterDto filters);
}
