package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank
    @Size(max = 64)
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(max = 32)
    private String phone;

    @NotNull
    @Size(max = 128)
    private String password;

    @NotBlank
    @Size(max = 4096)
    private String aboutMe;

    @NotNull
    private Long countryId;

    @NotNull
    private String city;

    @NotNull
    private PreferredContact notifyPreference;

    @Override
    public String toString() {
        return "UserRegistrationDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", countryId=" + countryId +
                ", city='" + city + '\'' +
                '}';
    }
}