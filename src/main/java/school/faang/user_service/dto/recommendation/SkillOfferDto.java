package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.Skill;

@Data
public class SkillOfferDto {

    private long id;
    private Skill skill;
}
