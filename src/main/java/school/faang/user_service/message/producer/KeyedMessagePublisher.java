package school.faang.user_service.message.producer;

public interface KeyedMessagePublisher extends MessagePublisher {
    void send(String topic, String key, Object message);
}
