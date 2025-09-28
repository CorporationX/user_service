package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

@Data
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
