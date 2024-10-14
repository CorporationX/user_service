package school.faang.user_service.publisher;

public interface EventPublisher<T> {
    void publish(T event);
}
