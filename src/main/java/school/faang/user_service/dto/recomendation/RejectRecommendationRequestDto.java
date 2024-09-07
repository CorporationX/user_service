package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectRecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Recommendation request rejection message can't be blank")
    private String reason;
}
