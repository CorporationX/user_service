package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    private Long id;
    private Skill skill;
    private Recommendation recommendation;
}
