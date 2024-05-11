package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillCandidateDto {

    private SkillDto skill;
    private long offersAmount;
}
