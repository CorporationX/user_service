package school.faang.user_service.dto.skill;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SkillCandidateDto {

    private final SkillDto skillDto;

    private final long offersAmount;
}
