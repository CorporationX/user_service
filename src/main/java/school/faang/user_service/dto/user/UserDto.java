package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Country;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Country country;
    private boolean active;
}