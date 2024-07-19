package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillOfferDto {
    @Positive(message="skillId should be positive")
    private Long skillId;
    @Positive(message="recommendationId should be positive")
    private Long recommendationId;
}