package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;

@Data
@NoArgsConstructor
public class SkillCandidateDto {
    @NotNull
    private SkillDto skill;

    @NotNull
    @Positive
    private Long offersAmount;
}
