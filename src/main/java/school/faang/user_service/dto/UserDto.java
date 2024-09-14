package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class UserDto {
    @Positive(message = "ID must be positive!")
    private long id;

    @NotBlank(message = "Username cannot be blank!")
    private String username;

    @Email(message = "Email should be valid!")
    private String email;
}
