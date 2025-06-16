package school.faang.user_service.kafka.events.publisher;

import school.faang.user_service.kafka.events.FollowerEvent;

public interface FollowerEventPublisher {
    void publish(FollowerEvent followerEvent);
}
