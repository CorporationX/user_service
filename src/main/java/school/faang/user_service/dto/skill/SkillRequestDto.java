package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillRequestDto {
    private Long id;
    private Long recommendationRequestId;
    private SkillDto skill;
}
