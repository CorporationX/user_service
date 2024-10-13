package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    @Positive(message = "SkillID must be positive")
    @NotNull(message = "SkillID cannot be null")
    private Long skillId;

    @Positive(message = "RecommendationID must be positive")
    @NotNull(message = "RecommendationID cannot be null")
    private Long recommendationId;
}
