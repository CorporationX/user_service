package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank
        @Schema(description = "Username пользователя")
        String username,
        @NotBlank
        @Email
        @Schema(description = "Email", example = "test@gmail.com")
        String email,
        @NotBlank
        @Schema(description = "Пароль")
        String password,
        @NotBlank
        @Schema(description = "Id страны пользователя", example = "1")
        Long countryId
) {
}
