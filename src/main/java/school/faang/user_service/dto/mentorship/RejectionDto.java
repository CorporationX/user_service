package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectionDto {
    @NotNull
    @Size(min = 1, max = 50)
    String reason;
}
