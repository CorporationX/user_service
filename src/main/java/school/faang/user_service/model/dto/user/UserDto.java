package school.faang.user_service.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.entity.contact.PreferredContact;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Incorrect email format")
    private String email;
    private String phone;
    private PreferredContact preference;
}
