package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillOfferDto {
    private Long id;
    private Long skillId;
    private Long recommendationId;
//    private String skillName;
}
