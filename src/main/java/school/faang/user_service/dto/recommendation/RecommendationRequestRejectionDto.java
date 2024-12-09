package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequestRejectionDto {

    @NotNull (message = "The reason can't be null")
    @NotBlank(message = "The reason for the refusal must be indicated")
    private String reason;
}
