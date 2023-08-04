package school.faang.user_service.util.filter.recommendationRequest;

import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filters);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filters);
}
