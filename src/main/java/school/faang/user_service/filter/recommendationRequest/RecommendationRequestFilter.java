package school.faang.user_service.filter.recommendationRequest;

import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplication(RecommendationRequestFilterDto recommendationRequestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RecommendationRequestFilterDto recommendationRequestFilterDto);
}
