package school.faang.user_service.kafka.events;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class Event {
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime occurredAt = LocalDateTime.now();
    private final String source = "user-service";
    private AnalyticsEventType eventType;
    private Long authorId;
    private Long receiverId;
    private Long userId;
}
