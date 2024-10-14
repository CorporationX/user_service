package school.faang.user_service.repository;

import school.faang.user_service.entity.recommendation.RecommendationRequest;

public interface RequestFilter {
    boolean apply(RecommendationRequest request);
}
