package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto{
    @Min(value = 0, message = "Id should be a positive value")
    @NotNull(message = "Id cannot be bull")
    private long id;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Email cannot be blank")
    private String email;
    private PreferredContact preference;
    private String phone;
}
