package school.faang.user_service.service.redis;

public interface MentorshipRequestedEventPublisher {
    void publish(String topic, String message);
}
