package school.faang.user_service.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Введите username")
        String username,
        @NotBlank(message = "Введите пароль")
        String password
) {
}
