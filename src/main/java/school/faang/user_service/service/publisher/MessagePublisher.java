package school.faang.user_service.service.publisher;

public interface MessagePublisher<T> {

    void publish(T message);

}
