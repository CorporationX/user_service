package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Controller;

@Data
public class SkillCandidateDto {
    @NotNull (message = "skill cannot be nill")
    private SkillCreateDto skill;
    long offersAmount;


}
