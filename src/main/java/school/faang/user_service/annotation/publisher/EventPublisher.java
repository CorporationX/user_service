package school.faang.user_service.annotation.publisher;

public interface EventPublisher {
    Class<?> getInstance();

    void publish(Object eventObject);
}
