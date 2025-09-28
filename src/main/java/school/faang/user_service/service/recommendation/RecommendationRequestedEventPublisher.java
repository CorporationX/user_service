package school.faang.user_service.service.recommendation;

import school.faang.user_service.event.RecommendationRequestedEvent;

public interface RecommendationRequestedEventPublisher {
    void publish(RecommendationRequestedEvent event);
}
