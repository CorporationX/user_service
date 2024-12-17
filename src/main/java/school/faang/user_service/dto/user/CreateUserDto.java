package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserDto {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    @NotNull
    @NotBlank
    private String phone;

    @NotNull
    @NotBlank
    private String password;

    @NotNull
    @NotBlank
    private String aboutMe;

    @NotNull
    @Min(0)
    private long countryId;

}
