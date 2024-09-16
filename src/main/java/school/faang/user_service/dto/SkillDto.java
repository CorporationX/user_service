package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SkillDto {
    @Positive(message = "ID must be positive")
    private Long id;

    @NotBlank(message = "Skill title cannot be null.")
    private String title;
}