package school.faang.user_service.dto.recommendation;

import lombok.Data;

@Data
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private String skillTitle;
    private Long recommendationId;
}
