package school.faang.user_service.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GoalCompletedEvent {
    private Long userId;
    private Long goalId;
    private LocalDateTime completedAt;
}
