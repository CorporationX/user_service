package school.faang.user_service.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class KafkaEvent {
    private UUID id;
    private AnalyticsEventType eventType;
    private LocalDateTime timestamp;

    public KafkaEvent(AnalyticsEventType analyticsEventType, LocalDateTime now) {

    }
}
