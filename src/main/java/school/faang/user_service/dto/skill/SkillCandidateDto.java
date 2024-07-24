package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;
}
