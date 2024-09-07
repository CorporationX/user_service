package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SkillDto(
        Long id,
        @NotBlank(message = "Skill title must not be empty")
        String title) {
}