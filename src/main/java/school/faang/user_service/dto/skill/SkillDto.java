package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.exception.DataValidException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    private Long id;
    private String title;

    public void validateSkill() {
        if (this.title.isBlank() || this.title.isEmpty()) {
            throw new DataValidException("Title cannot be empty");
        }
    }
}
