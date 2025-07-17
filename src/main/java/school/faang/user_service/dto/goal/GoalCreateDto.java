package school.faang.user_service.dto.goal;

import lombok.Data;

@Data
public class GoalCreateDto implements GoalRequest {
    private Long userId;
    private GoalDto goal;
}
