package school.faang.user_service.service.redis;

import school.faang.user_service.model.recommendation.RecommendationEvent;

public interface RedisService {
    void publish(RecommendationEvent message);
}
