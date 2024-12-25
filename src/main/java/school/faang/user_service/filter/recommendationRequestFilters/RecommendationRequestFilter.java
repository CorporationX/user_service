package school.faang.user_service.filter.recommendationRequestFilters;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto);
}
