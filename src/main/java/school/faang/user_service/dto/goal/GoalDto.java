package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
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
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long id;
    @NotNull(message = "title is required")
    @NotBlank
    private Long parentId;
    private String title;
    @NotNull(message = "description is required")
    @NotBlank
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long mentorId;
    private List<Long> usersId;
    private List<Long> skillsToAchieveId;
}
