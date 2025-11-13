package school.faang.user_service.filters.recommendation;

import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.stream.Stream;

public interface RecommendationFilter {
    default boolean isApplicable(FilterRecommendationRequestDto filterDto) {
        return filterDto != null;
    }

    Stream<Recommendation> apply(Stream<Recommendation> recommendations, FilterRecommendationRequestDto filters);

}
