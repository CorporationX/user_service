package school.faang.user_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "Содержит access-токен и информацию об его сроке действия")
@Builder
public record ResponseToken(
        @Schema(
                description = "JWT-токен, используемый для авторизации",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @NotBlank
        String value,

        @Schema(
                description = "Дата и время, когда токен истечёт",
                example = "2025-07-15T12:34:56"
        )
        @NotBlank
        LocalDateTime expireAt
) {
}
