package school.faang.user_service.publisher;

import org.springframework.stereotype.Component;

@Component
public interface EventPublisher<E> {
    void publish(E message);
}
