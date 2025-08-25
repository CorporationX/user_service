package school.faang.user_service.kafka.dto;

public record EnvelopeMessage<T>(
        String type,
        T payload
) {
}
