package school.faang.user_service.kafka_publisher;

public interface KafkaMessagePublisher<T>{
    void publish(T event);
}
