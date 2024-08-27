package school.faang.user_service.publisher.redis;

public interface MessagePublisher {

    void publishMessage(String message);
}
