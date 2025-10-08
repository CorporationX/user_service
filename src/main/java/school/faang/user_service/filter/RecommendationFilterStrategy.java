package school.faang.user_service.filter;

import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

public interface RecommendationFilterStrategy {

    boolean isApplicable(RecommendationFilterDto filters);

    boolean matchesFilters(Recommendation recommendation, RecommendationFilterDto filters);

}
