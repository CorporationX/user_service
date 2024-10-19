package school.faang.user_service.aspect;

public interface EventPublisher {
    Class<?> getInstance();

    void publish(Object eventObject);
}
