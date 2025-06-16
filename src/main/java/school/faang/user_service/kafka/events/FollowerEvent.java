package school.faang.user_service.kafka.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class FollowerEvent extends KafkaEvent {
    public enum TargetType {USER, PROJECT}

    private String followerId;
    private TargetType targetType;
    private String targetId;

    public FollowerEvent(String subscriberId, TargetType targetType, String targetId) {
        super(AnalyticsEventType.FOLLOW, LocalDateTime.now());
        this.followerId = subscriberId;
        this.targetType = targetType;
        this.targetId = targetId;
    }
}
