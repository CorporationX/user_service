package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Email cannot is blank")
    private String email;
}
