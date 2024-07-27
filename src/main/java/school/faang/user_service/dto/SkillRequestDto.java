package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SkillRequestDto {
    private long id;
    private RecommendationRequest request;
    private SkillDto skillDto;
}
