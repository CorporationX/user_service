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
    private Long followerId;
    private TargetType targetType;
    private Long targetId;
}
