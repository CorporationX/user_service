package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {
    @NotBlank(message = "Username is required")
    @Size(max = 64, message = "Username must be less than or equal to 64 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 64, message = "Email must be less than or equal to 64 characters")
    private String email;

    @Size(max = 32, message = "Phone number must be less than or equal to 32 characters")
    @Pattern(regexp = "^[+]?\\d{7,32}$", message = "Phone number is invalid")
    private String phone;

    @Size(max = 4096, message = "About Me section must be less than or equal to 4096 characters")
    private String aboutMe;

    @NotNull(message = "Active status is required")
    private Boolean active;

    @Size(max = 64, message = "City must be less than or equal to 64 characters")
    private String city;

    @NotNull(message = "Country ID is required")
    private Long countryId;

    private Integer experience;
}