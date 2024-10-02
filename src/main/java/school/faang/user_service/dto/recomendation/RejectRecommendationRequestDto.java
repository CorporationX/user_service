package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectRecommendationRequestDto {
    private Long id;
    @NotBlank(message = "Recommendation request rejection message can't be blank")
    private String reason;
}
