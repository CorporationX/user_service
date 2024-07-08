package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
@Builder
@Data
@Getter
public class SkillOfferDto {
    private Long skillId;
    private Long recommendationId;
}
