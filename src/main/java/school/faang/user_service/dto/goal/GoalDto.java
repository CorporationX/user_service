package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class GoalDto {

    private Long id;

    private String description;

    private Long parentId;

    @NotBlank
    private String title;

    private GoalStatus status;

    private LocalDateTime deadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Long> skillIds;
}
