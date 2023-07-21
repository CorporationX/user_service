package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private Long recommendationId;
}