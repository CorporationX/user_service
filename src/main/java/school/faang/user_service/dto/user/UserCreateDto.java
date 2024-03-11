package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private Long id;
    @NotNull
    @NotBlank(message = "Имя не может быть пустым")
    private String username;
    @NotNull
    @Email(message = "Не корректный почтовый адрес")
    private String email;
    @NotBlank
    @Size(min = 5, max = 11, message = "Поле 'phone' должно содержать от 5 до 11 символов")
    private String phone;
    @NotBlank
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    private String password;
    private Long countryId;
    private String userProfilePic;
}