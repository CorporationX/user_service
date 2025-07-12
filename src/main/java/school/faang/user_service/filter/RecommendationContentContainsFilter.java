package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

@Component
public class RecommendationContentContainsFilter implements RecommendationFilter {
    @Override
    public boolean isApplicable(RecommendationFilterDto recommendationFilterDto) {
        return recommendationFilterDto.contentContains() != null;
    }

    @Override
    public Stream<Recommendation> apply(
            Stream<Recommendation> recommendations,
            RecommendationFilterDto recommendationFilterDto
    ) {
        return recommendations
                .filter(
                        recommendation -> recommendation
                                .getContent()
                                .toLowerCase()
                                .contains(recommendationFilterDto.contentContains().toLowerCase())
                );
    }
}
