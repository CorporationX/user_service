package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto requestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                        RecommendationRequestFilterDto requestFilterDto);
}
