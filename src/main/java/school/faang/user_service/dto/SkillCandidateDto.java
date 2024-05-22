package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

@Data
@NoArgsConstructor
public class SkillCandidateDto {
    private SkillDto skill;
    private Long offersAmount;
}
