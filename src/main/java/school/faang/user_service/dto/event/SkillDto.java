package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.exception.DataValidationException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    private Long id;
    private String title;

    public void validateSkill() {
        if (this.title.isBlank() || this.title.isEmpty()) {
            throw new DataValidationException("Title cannot be empty");
        }
    }
}
