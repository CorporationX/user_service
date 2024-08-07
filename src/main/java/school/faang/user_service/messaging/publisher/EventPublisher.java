package school.faang.user_service.messaging.publisher;

import school.faang.user_service.event.RedisEvent;

public interface EventPublisher {

    void publish(RedisEvent event);
}