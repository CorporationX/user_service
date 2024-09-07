package school.faang.user_service.service.recomendation.filters;

import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(FilterRecommendationRequestsDto filter);

    Stream<RecommendationRequest> apply(
        Stream<RecommendationRequest> requestStream,
        FilterRecommendationRequestsDto filter
    );
}
