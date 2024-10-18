package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedEvent {
    private Long userId;
    private Long goalId;
    private LocalDateTime createdAt;
}
