package school.faang.user_service.kafka;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public abstract class Event {
    private final UUID id = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String source = "user-service";
    private String traceId;
    private String eventType;
    private Long authorId;
    private Long receiverId;
    private Long userId;
}
