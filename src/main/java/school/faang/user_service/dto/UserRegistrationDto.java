package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRegistrationDto(

        @NotBlank(message = "Username cannot be blank")
        @NotNull(message = "Username cannot be null")
        String username,

        @NotNull
        @Size(min = 12, max = 64, message = "The password must be between 12 and 64 characters long")
        String password,

        @Email(message = "Invalid email")
        @NotNull(message = "Email cannot be null")
        String email,

        @Size(max = 32, message = "The length of the phone must not exceed 32 characters")
        String phone,

        @Size(max = 4096, message = "The length of the about me must not exceed 4096 characters")
        String aboutMe,

        @Size(max = 64, message = "The length of the city must not exceed 64 characters")
        String city,

        @NotNull(message = "Country id cannot be null")
        Long countryId
) {
}
