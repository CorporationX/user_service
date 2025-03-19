package school.faang.user_service.dto.skill;

import lombok.Data;

@Data
public class SkillCandidateDto {
    private SkillDto skill;
    private long offerAmount;
}
