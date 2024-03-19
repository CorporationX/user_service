package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotBlank(message = "Username can't be empty")
    private String username;
    @NotBlank(message = "E-mail can't be empty")
    private String email;
    @NotBlank(message = "Phone can't be empty")
    private String phone;
    @NotBlank(message = "Password can't be empty")
    private String password;
    private boolean active;
    private boolean isPremium;
    private Long countryId;
}
