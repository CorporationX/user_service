package school.faang.user_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsCreatedEvent {
    private long id;
    private long receiverId;
    private long actorId;
    private EventType eventType;
}
