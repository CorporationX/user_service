package school.faang.user_service.filters.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

@Component
public class RecommendationContentFilter implements RecommendationFilter {
    @Override
    public Stream<Recommendation> apply(Stream<Recommendation> recommendations,
                                        FilterRecommendationRequestDto filters) {
        if (filters.contentContains() == null || filters.contentContains().isBlank()) {
            return recommendations;
        }
        return recommendations.filter(r -> {
            String content = r.getContent();
            return content != null && content.toLowerCase()
                    .contains(filters.contentContains().toLowerCase());
        });
    }

}
