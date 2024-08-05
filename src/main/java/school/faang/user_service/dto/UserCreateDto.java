package school.faang.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private Long id;

    @NotBlank
    @Max(64)
    private String username;

    @NotBlank
    @Email
    @Max(64)
    private String email;

    @NotBlank
    @Max(128)
    private String password;

    @NotNull
    private boolean active;

    @NotNull
    @Positive
    private Long countryId;

    private UserProfilePicDto userProfilePicDto;

}
