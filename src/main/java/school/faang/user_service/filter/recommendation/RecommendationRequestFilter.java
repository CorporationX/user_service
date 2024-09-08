package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {

    boolean isApplicable(RequestFilterDto filters);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filters);
}
