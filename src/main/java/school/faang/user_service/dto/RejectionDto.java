package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectionDto {
    @NotNull(message = "Request id can't be null")
    private Long requestId;

    private String status;
    private String reason;
}
