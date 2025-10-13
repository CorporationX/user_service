package school.faang.user_service.dto.goal;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GoalUpdateDto(
        @Nullable
        String title,
        @Nullable
        String description,
        @Nullable
        @Future
        LocalDateTime deadline,
        @Nullable
        Long mentorId,
        @Nullable
        GoalStatus status,
        @Nullable
        List<Long> skillIds
) {

}
