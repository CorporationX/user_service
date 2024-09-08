package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Username cannot is blank")
    private String username;
    @NotBlank(message = "Email cannot is blank")
    private String email;
}
