package school.faang.user_service.messaging;

public interface MessagePublisher<T> {
     void publish(T message);
}
