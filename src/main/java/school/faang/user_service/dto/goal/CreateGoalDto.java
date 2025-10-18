package school.faang.user_service.dto.goal;

import java.time.LocalDateTime;
import java.util.List;

public record CreateGoalDto(
        String title,
        String description,
        LocalDateTime deadline,
        Long mentorId,
        List<Long> userIds
) {
}