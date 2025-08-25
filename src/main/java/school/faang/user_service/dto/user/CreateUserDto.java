package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @NotBlank(message = "Enter your name")
        @Schema(description = "User name", example = "Ivan")
        String username,

        @NotBlank(message = "Enter your email")
        @Email(message = "Enter your email")
        @Schema(description = "User email", example = "Ivan@gmail.com")
        String email,

        @Size(min = 8, message = "Please enter a password of at least 8 characters")
        @Schema(description = "User password", example = "At least 8 characters")
        String password,

        @NotNull(message = "Enter your country id")
        @Schema(description = "User countryId", example = "1")
        Long countryId
) {
}
