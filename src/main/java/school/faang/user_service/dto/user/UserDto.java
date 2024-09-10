package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.UserProfilePic;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank
    @Size(min = 5, max = 50)
    private String username;
    @NotBlank
    @Size(min = 10, max = 50)
    private String email;
    @NotBlank
    @Size(min = 8, max = 50)
    private String password;
    @NotBlank
    private Long countryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long premiumId;
    private UserProfilePic userProfilePic;
}
