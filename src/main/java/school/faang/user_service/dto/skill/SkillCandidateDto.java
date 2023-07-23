package school.faang.user_service.dto.skill;

import lombok.Data;

@Data
public class SkillCandidateDto {
    private final SkillDto skill;
    private final long offersAmount;
}
