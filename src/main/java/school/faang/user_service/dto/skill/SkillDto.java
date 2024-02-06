package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class SkillDto {
    private long id;
    private String title;
}
