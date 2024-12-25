package school.faang.user_service.message.producer;

public interface MessagePublisher {
    void send(String channel, Object message);
}
