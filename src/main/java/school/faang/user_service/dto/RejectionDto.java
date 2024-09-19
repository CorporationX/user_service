package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectionDto {
    @NotNull(message = "reason can't be null")
    private String reason;
}
