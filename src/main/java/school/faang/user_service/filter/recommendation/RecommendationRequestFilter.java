package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    public boolean isApplicable(RecommendationRequestFilterDto filterDto);

    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RecommendationRequestFilterDto filterDto);
}
