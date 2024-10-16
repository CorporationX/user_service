package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    // Здесь не все поля у сущности юзер
    private String phone;
    private String avatar;
    private String avatarSmall;
    private PreferredContact preference;
}
