package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SkillDto {
    private String title;
    private Long userId;
}
