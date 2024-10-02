package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.group.Create;
import school.faang.user_service.service.group.Update;

import java.util.List;

public record GoalDto(@NotBlank(message = "Title cannot be blank", groups = Create.class)
                      @Size(max = 64, message = "Title cannot be longer than 64")
                      String tittle,
                      @NotBlank(message = "Description cannot be blank", groups = Create.class)
                      @Size(max = 4096, message = "Description cannot be longer than 64")
                      String description,
                      Long parentId,
                      @NotEmpty(groups = {Create.class, Update.class})
                      List<String> titleSkills,
                      @NotNull(message = "Status cannot be null")
                      GoalStatus status) {
}
