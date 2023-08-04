package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SkillOfferDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Skill id should be determined")
    private Long skillId;

    @NotNull(message = "Recommendation id should be determined")
    private Long recommendationId;
}
