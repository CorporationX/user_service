package school.faang.user_service.dto;

import lombok.*;
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
