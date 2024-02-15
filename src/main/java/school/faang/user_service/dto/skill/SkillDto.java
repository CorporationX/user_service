package school.faang.user_service.dto.skill;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillDto {
    private final Long id;
    private final String title;
}
