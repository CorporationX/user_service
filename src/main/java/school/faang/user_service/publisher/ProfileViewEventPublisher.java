package school.faang.user_service.publisher;


public interface ProfileViewEventPublisher {
    void publish(String message);
}
