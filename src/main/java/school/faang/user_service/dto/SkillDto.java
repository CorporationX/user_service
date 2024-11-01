package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SkillDto(
        Long id,
        @NotBlank(message = "title не может быть пустым или null")
        String title) {
}
