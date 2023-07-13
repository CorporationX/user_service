package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.exception.DataValidationException;

@Data
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;

    public void validateSkill() {
        if (this.title.isBlank()) {
            throw new DataValidationException("Title cannot be empty");
        }
    }
}
