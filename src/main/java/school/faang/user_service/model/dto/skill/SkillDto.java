package school.faang.user_service.model.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

@Builder
public record SkillDto(
        @Null(groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long id,
        @NotBlank(message = "Skill title must not be empty")
        String title) {
}