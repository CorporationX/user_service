package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class SkillCreateDto {
    @NotNull(message = "title cannot be null ")
    private String title;


}
