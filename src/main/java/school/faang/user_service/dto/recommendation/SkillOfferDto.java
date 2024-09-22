package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillOfferDto {
    @Positive(message = "skillId should be a positive number")
    private Long skillId;
    @Positive(message = "recommendationId should be a positive number")
    private Long recommendationId;
}
