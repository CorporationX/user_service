package school.faang.user_service.dto.recommendationRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionDto {
    @NotBlank
    private String reason;
}