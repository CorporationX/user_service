package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotNull(message = "Name cannot be null!")
    private String username;
    @Email(message = "Email must be valid!")
    private String email;
    @NotNull(message = "Phone cannot be null!")
    private String phone;
}
