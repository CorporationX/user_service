package school.faang.user_service.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class FollowerEvent extends Event {

    public enum TargetType {USER, PROJECT}
    private String followerId;
    private TargetType targetType;
    private String targetId;
}
