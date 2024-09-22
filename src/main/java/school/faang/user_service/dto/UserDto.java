package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDto {
    @NotBlank
    private long id;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
}
