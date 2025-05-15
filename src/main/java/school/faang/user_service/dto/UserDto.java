package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserDto {
    @NotNull(message = "ID is required")
    @Positive(message = "ID must be positive")
    private Long id;
    @NotEmpty(message = "Username is required")
    private String username;
    @Email(message = "Email must be valid")
    private String email;
}
