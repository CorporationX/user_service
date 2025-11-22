package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Locale;

public record CreateUserDto(
        @NotBlank(message = "Name should be present!")
        String username,
        @NotBlank(message = "Email should be present!")
        String email,
        @NotBlank(message = "Password should be present!")
        String password,
        @NotNull(message = "Country id should be present!")
        Long countryId,
        Locale locale
) {
}
