package school.faang.user_service.kafka.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record EnvelopeMessage(
        String type,
        JsonNode payload
) {
}
