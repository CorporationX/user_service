package school.faang.user_service.dto.event.redis;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionEvent extends Event {
    private Long projectId;
    private Long followerId;
    private Long followeeId;
    private SubscriptionEventType eventType;
}
