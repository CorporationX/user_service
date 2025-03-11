package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "DTO для создания пользователя")
public class UserCreateDto {
    @NotEmpty(message = "Имя не может быть пустым")
    @Size(max = 64, message = "Имя не должно быть длиннее 64 символов")
    @Schema(description = "Имя пользователя", example = "JohnDoe")
    private String username;

    @NotEmpty(message = "Почта не может быть пустой")
    @Email(message = "Некорректный формат почты")
    @Schema(description = "Email пользователя", example = "johndoe@example.com")
    private String email;

    @NotEmpty(message = "Телефон не может быть пустым")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Телефон должен содержать от 10 до 15 цифр и может начинаться с +")
    @Schema(description = "Номер телефона пользователя", example = "+12345678901")
    private String phone;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Length(min = 8, max = 128, message = "Пароль должен содержать от 8 до 128 символов")
    @Schema(description = "Пароль пользователя", example = "StrongPassword123")
    private String password;

    @Schema(description = "Подтверждение пароля", example = "StrongPassword123")
    private String confirmPassword;

    @AssertTrue(message = "Необходимо согласиться с условиями")
    @Schema(description = "Флаг согласия с условиями", example = "true")
    private boolean agreeToTerms;

    @NotNull
    @Schema(description = "ID страны пользователя", example = "1")
    private Long countryId;

    @Schema(description = "Аватар пользователя (изображение)")
    private MultipartFile profilePic;
}
