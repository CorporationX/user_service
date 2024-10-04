package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SkillDto {
    private long id;
    @NotBlank
    private String title;

    public boolean validateTitle() {
        return !title.trim().isBlank();
    }
}
