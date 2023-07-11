package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillDto {
    private Long id;
    @NotEmpty(message = "Title can't be empty")
    @Size(max = 100, message = "Title should be at least 3 symbols short")
    private String title;
}
