package school.faang.user_service.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.Country;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank
    @Size(min = 1, max = 64)
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Max(128)
    private String password;
    @Size(min = 1, max = 10)
    private String phone;
    @Max(4096)
    private String aboutMe;
    private Country country;
    @Max(64)
    private String city;
    private Integer experience;
}
