package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private long id;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @JsonIgnore
    private String password;
    private String phone;
    @NotNull
    private long countryId;
    private PreferredContact preference;

}