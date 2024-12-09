package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillCandidateDto {

    @NotNull
    private SkillDto skillDto;

    @NotNull
    private long offerAmount;
}
