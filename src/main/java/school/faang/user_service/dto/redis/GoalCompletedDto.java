package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalCompletedDto {
    private int userId;
    private int goalId;
    private LocalDateTime completedAt;
}
