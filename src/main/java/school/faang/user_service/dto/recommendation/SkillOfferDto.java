package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
@Builder
@Data
@Getter
public class SkillOfferDto {
    @Positive(message="skillId should be positive")
    private Long skillId;
    @Positive(message="recommendationId should be positive")
    private Long recommendationId;
}