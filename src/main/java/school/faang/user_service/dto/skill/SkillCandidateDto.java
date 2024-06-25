package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillCandidateDto {
    @NotNull
    private SkillDto skillDto;
    @NotNull
    private long offersAmount;
}