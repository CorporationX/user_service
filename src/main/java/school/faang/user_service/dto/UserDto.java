package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotBlank(message = "Username can't be empty")
    private String username;
    @NotBlank(message = "E-mail can't be empty")
    @Email
    private String email;
    @NotBlank(message = "Phone can't be empty")
    @Pattern(regexp = "^\\+\\d{11}$")
    private String phone;
    @NotBlank(message = "Password can't be empty")
    private String password;
    private boolean active;
    @NotNull(message = "Country ID can't be empty")
    private Long countryId;
    private PreferredContact contactPreference;
}
