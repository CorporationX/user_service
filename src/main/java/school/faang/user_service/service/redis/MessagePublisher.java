package school.faang.user_service.service.redis;

public interface MessagePublisher {
    void publish(String topic, String message);
}
