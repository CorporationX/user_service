package school.faang.user_service.redis;

public interface MessagePublisher {

    void publish(String message);
}
