package school.faang.user_service.dto.skill;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private Long recommendationId;
}
