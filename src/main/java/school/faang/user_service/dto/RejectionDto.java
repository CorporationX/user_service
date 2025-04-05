package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectionDto {
    @NotNull
    private String reason;
}