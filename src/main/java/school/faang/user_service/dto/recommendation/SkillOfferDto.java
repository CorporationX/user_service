package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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