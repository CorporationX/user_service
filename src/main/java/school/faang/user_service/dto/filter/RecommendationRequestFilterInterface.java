package school.faang.user_service.dto.filter;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilterInterface {
    boolean isApplicable(school.faang.user_service.dto.recommendation.RecommendationRequestFilter filter);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RecommendationRequestFilter filter);
}
