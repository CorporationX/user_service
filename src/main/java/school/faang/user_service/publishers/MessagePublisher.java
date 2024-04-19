package school.faang.user_service.publishers;

public interface MessagePublisher {
    <T> void publish(T event);
}
