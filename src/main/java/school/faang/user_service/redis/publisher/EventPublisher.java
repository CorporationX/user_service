package school.faang.user_service.redis.publisher;

public interface EventPublisher<T> {
    void publish(T event);
}
