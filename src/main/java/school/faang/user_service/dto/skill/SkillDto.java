package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@NotNull(message = "Skill title cannot be null")
@NotBlank(message = "Skill title cannot be blank")
public class SkillDto {
    private long id;
    private String title;
}
