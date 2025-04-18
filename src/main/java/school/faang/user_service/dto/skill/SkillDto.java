package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SkillDto(
        Long id,
        @NotBlank
        @Size(max = 50, message = "The skill name cannot be longer than 50 characters")
        String title
) {
}
