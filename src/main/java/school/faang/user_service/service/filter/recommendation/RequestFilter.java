package school.faang.user_service.service.filter.recommendation;

import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.List;
import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto dto);

    Stream<RecommendationRequest> apply(RequestFilterDto dto, Stream<RecommendationRequest> recommendationRequests);

}
