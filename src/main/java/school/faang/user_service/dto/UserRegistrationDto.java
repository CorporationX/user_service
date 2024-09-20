package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRegistrationDto(

        @NotBlank
        @NotNull
        String username,

        @NotNull
        @Size(min = 12, max = 64)
        String password,

        @Email
        @NotNull
        String email,

        @Max(32)
        String phone,

        @Max(4096)
        String aboutMe,

        @Max(64)
        String city,

        @NotNull
        Long countryId
) {
}
