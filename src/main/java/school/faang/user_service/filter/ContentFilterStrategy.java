package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Component
public class ContentFilterStrategy implements RecommendationFilterStrategy {

    @Override
    public boolean isApplicable(RecommendationFilterDto filters) {
        return filters.contentContains() != null;
    }

    @Override
    public boolean matchesFilters(Recommendation recommendation, RecommendationFilterDto filters) {
        return recommendation.getContent()
                .toLowerCase()
                .contains(filters.contentContains().toLowerCase());
    }

}
