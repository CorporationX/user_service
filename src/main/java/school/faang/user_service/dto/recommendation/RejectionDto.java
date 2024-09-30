package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RejectionDto {
    @NotBlank
    private String reason;
}
