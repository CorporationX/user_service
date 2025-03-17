package school.faang.user_service.entity.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record SearchGoalDto(
        String title,
        GoalStatus status,
        LocalDateTime updatedAt) {
}
