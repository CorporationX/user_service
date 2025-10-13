package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

public record GoalFilterDto(
        @Nullable
        String titleContains,
        @Nullable
        String descriptionContains,
        @Nullable
        GoalStatus status,
        @Nullable
        Long mentorId,
        @Nullable
        List<Long> skillIds
) {
}
