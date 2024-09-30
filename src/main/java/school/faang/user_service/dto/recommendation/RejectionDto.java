package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectionDto {
    @NotNull(message = "Reason cannot be blank.")
    private String reason;
}
