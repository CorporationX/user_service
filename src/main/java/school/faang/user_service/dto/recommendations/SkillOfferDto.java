package school.faang.user_service.dto.recommendations;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;

/**
 * @author Alexander Bulgakov
 */
@Data
public class SkillOfferDto {
    private long id;
    public long skillId;
    private long recommendationId;
}
