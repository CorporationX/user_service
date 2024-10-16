package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillCandidateDto {
    SkillDto skill;
    long offersAmount;
}
