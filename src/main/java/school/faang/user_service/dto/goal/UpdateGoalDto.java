package school.faang.user_service.dto.goal;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGoalDto {

    @NotNull(message = "goal id must not be null")
    private Long id;

    private String title;

    private String description;

    private LocalDateTime deadline;

    private List<Long> skillsToAchieveIds;

    @Enumerated(value = EnumType.ORDINAL)
    private GoalStatus status;
}
