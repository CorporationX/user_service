package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount = 1;


    public void addOffersAmount(){
        this.offersAmount+=1;
    }
}
