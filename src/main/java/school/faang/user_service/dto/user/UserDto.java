package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
}
