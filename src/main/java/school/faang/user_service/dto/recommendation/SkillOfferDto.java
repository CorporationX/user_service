package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    @Positive(message = "skillId should be positive number")
    private Long skillId;
    @Positive(message = "recommendationId should be positive number")
    private Long recommendationId;
}
