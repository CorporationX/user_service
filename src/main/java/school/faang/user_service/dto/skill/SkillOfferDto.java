package school.faang.user_service.dto.skill;

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
    @Positive(message = "skillId должно быть положительным число")
    private Long skillId;
    @Positive(message = "recommendationId должно быть положительным число")
    private Long recommendationId;
}