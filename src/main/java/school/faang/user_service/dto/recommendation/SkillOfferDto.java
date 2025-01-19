package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SkillOfferDto {
    Long id;

    @NotNull(message = "The skillId cannot be null")
    Long skillId;

    Long recommendationId;
}
