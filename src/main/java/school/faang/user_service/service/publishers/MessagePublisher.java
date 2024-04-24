package school.faang.user_service.service.publishers;

public interface MessagePublisher<T> {

    void publish(T message);

}
