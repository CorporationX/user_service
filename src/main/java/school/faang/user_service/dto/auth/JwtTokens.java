package school.faang.user_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record JwtTokens(
        @NotBlank
        Token accessToken,
        @NotBlank
        Token refreshToken
) {
}
