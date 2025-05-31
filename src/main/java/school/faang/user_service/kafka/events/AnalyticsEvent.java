package school.faang.user_service.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {
    private long id;
    private long receiverId;
    private long actorId;
    private AnalyticsEventType analyticsEventType;
}
