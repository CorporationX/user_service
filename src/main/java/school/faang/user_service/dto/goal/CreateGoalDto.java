package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record CreateGoalDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        LocalDateTime deadline,
        Long mentorId,
        @NotBlank
        List<Long> userIds
) {

}
