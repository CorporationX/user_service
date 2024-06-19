package school.faang.user_service.publisher;

public interface MessagePublisher<T extends Event> {
    void publish(T event);
}