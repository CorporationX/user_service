package school.faang.user_service.exception.kafka;

public class KafkaPublishException extends RuntimeException {
    public KafkaPublishException(String message, Exception cause) {
        super(message, cause);
    }
}
