package school.faang.user_service.publisher;

import school.faang.user_service.event.Event;

public interface MessagePublisher<T extends Event> {
    void publish(T event);
}