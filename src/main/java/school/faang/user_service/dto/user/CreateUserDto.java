package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
        @NotBlank(message = "Введите ваше имя")
        String username,
        @Email(message = "Введите email")
        String email,
        @Size(min = 8, message = "Введите пароль не меньше 8 символов")
        String password,
        @NotBlank(message = "Введите id вашей страны")
        Long countryId
) {
}
