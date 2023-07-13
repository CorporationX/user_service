package school.faang.user_service.dto.skill;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class SkillDto {
    private Long id;
    private String title;
}
