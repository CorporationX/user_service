package school.faang.user_service.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoalSetEventDto {
    private Long goalId;
    private Long userId;
}
