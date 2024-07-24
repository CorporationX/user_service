package school.faang.user_service.dto.recommendationRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionRequestDto {
    @NotBlank
    private String reason;
}