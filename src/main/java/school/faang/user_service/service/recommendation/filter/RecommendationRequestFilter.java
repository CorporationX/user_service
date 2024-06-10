package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto filter);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RecommendationRequestFilterDto filter);
}
