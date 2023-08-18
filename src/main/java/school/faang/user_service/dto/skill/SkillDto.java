package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    @NotBlank(message = "Skill can't be created with empty name")
    @Size(max = 64, message = "Skill's title length can't be more than 64 symbols")
    private String title;
}
