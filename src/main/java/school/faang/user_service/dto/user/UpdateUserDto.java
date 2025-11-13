package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import jakarta.validation.constraints.Size;

@Builder
public record UpdateUserDto(
        @Size(min = 1, max = 50)
        @Pattern(regexp = "^[A-Za-z0-9._-]+$",
                message = "Username may contain letters, digits, dot, underscore, hyphen")
        String username,

        @Email
        String email,

        @Size(max = 30)
        String phone,

        @Size(max = 500)
        String aboutMe,

        @Positive
        Long countryId,

        @Size(max = 100)
        String city
) {
}