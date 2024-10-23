package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@NoArgsConstructor
public class UserDto {
    @Positive(message = "Id must be a positive number")
    private Long id;
    @NotBlank
    private String username;
    @Email
    private String email;

    private String avatar;

    private String avatarSmall;

    @Size(max = 32, message = "Phone size cant be longer than 32 symbols")
    private String phone;
    @Size(max = 32, message = "Preference size cant be longer than 32 symbols")
    private PreferredContact preference;
}
