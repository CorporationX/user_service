package school.faang.user_service.filter;

import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto requestRequestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                        RecommendationRequestFilterDto recommendationRequestFilterDto);
}