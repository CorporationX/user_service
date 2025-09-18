package school.faang.user_service.service.event;

import school.faang.user_service.entity.event.EventStartEvent;

public interface MessagePublisher {
    void publish(EventStartEvent event);
}