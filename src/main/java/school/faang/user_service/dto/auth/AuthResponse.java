package school.faang.user_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthResponse(
        @NotBlank
        String token
) {
}
