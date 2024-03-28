package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Size(min = 5, max = 25, message = "Username length must be between 5 and 25 characters")
    private String username;
    @NotBlank(message = "E-mail can't be empty")
    @Email(message = "E-mail must match the pattern email@example.com")
    private String email;
    @NotBlank(message = "Phone can't be empty")
    @Pattern(regexp = "^\\+\\d{11}$", message = "Phone number must be formatted (ex. +71234567890)")
    private String phone;
    @NotBlank(message = "Password can't be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    private boolean active;
    private boolean isPremium;
    @NotNull(message = "CountryID can't be empty")
    @Positive(message = "CountryID can't be less than 1")
    private Long countryId;
}