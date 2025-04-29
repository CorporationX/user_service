package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SkillViewDto {
    @NotNull(message = "id cannot be null")
    private Long id;

    @NotNull(message = "title cannot be null ")
    private String title;

}
