package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;

    public void addOffersAmount() {
        this.offersAmount += 1;
    }
}
