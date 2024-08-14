package school.faang.user_service.messaging.publisher;

public interface EventPublisher<T> {

    void publish(T event);
}