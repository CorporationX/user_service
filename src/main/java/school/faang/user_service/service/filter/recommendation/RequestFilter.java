package school.faang.user_service.service.filter.recommendation;

import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.List;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto dto);

    List<RecommendationRequest> apply(RequestFilterDto dto, List<RecommendationRequest> recommendationRequests);

}
