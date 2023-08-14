package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RejectionDto {
    @NotEmpty(message = "Reason cannot be null")
    private String reason;
}
