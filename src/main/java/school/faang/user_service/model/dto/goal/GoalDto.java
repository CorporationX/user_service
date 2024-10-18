package school.faang.user_service.model.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.model.enums.GoalStatus;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

import java.util.List;

@Builder
public record GoalDto(
        @NotBlank(message = "Title cannot be blank", groups = {CreateGroup.class, UpdateGroup.class})
        @Size(max = 64, message = "Title cannot be longer than 64")
        String title,

        @NotBlank(message = "Description cannot be blank", groups = CreateGroup.class)
        @Size(max = 4096, message = "Description cannot be longer than 64")
        String description,
        Long parentId,

        @NotEmpty(groups = {CreateGroup.class, UpdateGroup.class})
        List<Long> skillIds,

        GoalStatus status) {
}
