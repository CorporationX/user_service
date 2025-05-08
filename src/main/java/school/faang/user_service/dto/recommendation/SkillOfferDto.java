package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private Long recommendationId;
}
