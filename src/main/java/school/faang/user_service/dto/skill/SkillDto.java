package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class SkillDto {
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotNull(message = "The skill title cannot be null")
    @NotBlank(message = "The skill title cannot be empty")
    private String title;
}
