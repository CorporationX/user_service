package school.faang.user_service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Token(
        @NotBlank
        String value,
        @NotBlank
        LocalDateTime expireAt,
        @NotBlank
        long expiration
) {
}
