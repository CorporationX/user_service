package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecommendationRequestRejectionDto {
    @NotBlank(message = "reason cannot be blank")
    private String reason;
}
