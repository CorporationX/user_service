package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record SkillOfferDto(
        @NotNull(message = "Id of skill in SkillOfferDto can't be null")
        @Positive(message = "Id of skill in SkillOfferDto can't be negative")
        long skillId
) {
}
