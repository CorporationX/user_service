package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    @NotBlank(message = "Skill title can't be empty")
    @Size(max = 150, message = "Skill title can't be longer than 150 characters")
    private String title;
}
