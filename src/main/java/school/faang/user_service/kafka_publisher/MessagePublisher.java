package school.faang.user_service.kafka_publisher;

public interface MessagePublisher<T>{
    void publish(T event);
}
