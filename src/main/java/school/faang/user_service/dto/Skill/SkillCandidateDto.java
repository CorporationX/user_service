package school.faang.user_service.dto.Skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;
}
