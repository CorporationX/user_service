package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecommendationRequestRejectionDto {
    @NotBlank(message = "Recommendation request rejection message can't be blank")
    private String reason;
}
