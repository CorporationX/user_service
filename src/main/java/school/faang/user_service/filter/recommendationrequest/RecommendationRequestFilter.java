package school.faang.user_service.filter.recommendationrequest;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filterDto);
    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto);
}