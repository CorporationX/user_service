package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionDto {
    @NotBlank
    private String reason;
}
