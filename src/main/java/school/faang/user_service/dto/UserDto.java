package school.faang.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    @Positive
    private Long id;

    @NotBlank
    @Size(max = 64)
    private String username;

    @Email
    @Size(max = 64)
    private String email;

    @Valid
    private UserProfilePicDto userProfilePicDto;
}