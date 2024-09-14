package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectionDto {
    @NotNull(message = "reason can't be null")
    private String reason;
}
