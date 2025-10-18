package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record UpdateGoalDto(
        String title,
        String description,
        LocalDateTime deadline,
        Long mentorId,
        GoalStatus status
) {
}
