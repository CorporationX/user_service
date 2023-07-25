package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectionReasonDto {
    @NotNull
    @NotEmpty(message = "Rejection reason is required")
    private String reason;
}
