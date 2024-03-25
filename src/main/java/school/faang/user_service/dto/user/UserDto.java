package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
    @Email(message = "Email must be format: email@example.com")
    private String email;

    @NotBlank(message = "Phone can't be empty")
    @Pattern(regexp = "^\\+\\d{11}$")
    private String phone;

    @NotBlank(message = "Password can't be empty")
    private String password;

    private boolean active;

    private boolean isPremium;

    @Positive(message = "CountryId can't be less than 1")
    private Long countryId;
}