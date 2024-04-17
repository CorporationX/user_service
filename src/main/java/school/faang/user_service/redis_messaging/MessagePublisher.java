package school.faang.user_service.redis_messaging;

public interface MessagePublisher<T> {

    void publish (T message);

}
