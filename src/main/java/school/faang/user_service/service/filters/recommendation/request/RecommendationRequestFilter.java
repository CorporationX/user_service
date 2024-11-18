package school.faang.user_service.service.filters.recommendation.request;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filters);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recRequests, RequestFilterDto filters);
}
