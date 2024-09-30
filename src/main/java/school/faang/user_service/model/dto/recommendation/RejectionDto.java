package school.faang.user_service.model.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RejectionDto {
    @NotBlank
    private String reason;
}
