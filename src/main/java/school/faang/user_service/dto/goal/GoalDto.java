package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.validation.group.Create;
import school.faang.user_service.dto.validation.group.Update;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    @NotNull(message = "User id cannot be null", groups = {Create.class})
    private Long userId;

    @NotNull(message = "Status cannot be null", groups = Update.class)
    private Long goalId;

    @NotNull(message = "Title cannot be null", groups = {Create.class, Update.class})
    @NotBlank(message = "Title cannot be blank", groups = {Create.class, Update.class})
    @Size(max = 64, message = "Title cannot be longer than 64 characters", groups = {Create.class, Update.class})
    private String title;

    @NotNull(message = "Description cannot be null", groups = {Create.class, Update.class})
    @NotBlank(message = "Description cannot be blank", groups = {Create.class, Update.class})
    @Size(max = 128, message = "Description cannot be longer than 128 characters", groups = {Create.class, Update.class})
    private String description;

    private Long parentGoalId;

    private List<Long> skillIds;

    @NotNull(message = "Status cannot be null", groups = Update.class)
    private GoalStatus status;
}
