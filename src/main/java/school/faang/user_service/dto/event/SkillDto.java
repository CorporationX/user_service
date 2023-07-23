package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.exception.DataValidException;

@Data
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;

    public void validateSkill() {
        if (this.title.isBlank()) {
            throw new DataValidException("Title cannot be empty");
        }
    }
}
