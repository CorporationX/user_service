package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank(message = "Введите username")
        @Schema(description = "Username пользователя")
        String username,

        @NotBlank(message = "Введите email")
        @Email(message = "Некорректный email")
        @Schema(description = "Email", example = "test@gmail.com")
        String email,

        @NotBlank(message = "Введите пароль")
        @Schema(description = "Пароль")
        String password,

        @NotBlank(message = "Не определена страна пользователя")
        @Schema(description = "Id страны пользователя", example = "1")
        Long countryId
) {
}
