package school.faang.user_service.entity.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillCandidateDto {

    private SkillDto skill;

    private Long offersAmount;
}
