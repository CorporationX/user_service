package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.UserProfilePic;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;

    @NotBlank
    @Size(max = 64, message = "username should be less than 65 symbols")
    private String username;

    @NotBlank
    @Size(max = 64, message = "email should be less than 65 symbols")
    private String email;

    @NotBlank
    @Size(max = 128, message = "password should be less than 129 symbols")
    private String password;
    private Long countryId;
    private boolean active;
    private UserProfilePic userProfilePic;
}