package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SkillCandidateDto {

    private SkillDto skillDto;
    private long offersAmount;
}
