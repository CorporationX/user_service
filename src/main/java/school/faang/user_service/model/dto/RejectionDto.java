package school.faang.user_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RejectionDto {
    @NotBlank(message = "Rejection reason must not be null or empty")
    @Size(max = 4096)
    String reason;
}
