package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    public abstract void publish(T event);
}
