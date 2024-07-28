package school.faang.user_service.dto.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Goal Dto")
public class GoalDto {

    @Schema(description = "goal id")
    private Long id;
    @Schema(description = "parent goal id")
    private Long parentId;
    @Schema(description = "title")
    private String title;
    @Schema(description = "description")
    @NotBlank
    private String description;
    @Schema(description = "status")
    private GoalStatus status;
    @Schema(description = "deadline")
    private LocalDateTime deadline;
    @Schema(description = "created at")
    private LocalDateTime createdAt;
    @Schema(description = "updated at")
    private LocalDateTime updatedAt;
    @Schema(description = "mentor id")
    private Long mentorId;
    @Schema(description = "users id")
    private List<Long> userIds;
    @Schema(description = "skills id")
    private List<Long> skillsToAchieveIds;
}
