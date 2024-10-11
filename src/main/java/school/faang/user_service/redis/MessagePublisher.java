package school.faang.user_service.redis;

public interface MessagePublisher <T> {

    void publish(T message);
}
