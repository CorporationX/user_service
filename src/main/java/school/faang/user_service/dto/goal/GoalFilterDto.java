package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

public record GoalFilterDto(
        String title,
        GoalStatus status,
        Long skillId
) {}