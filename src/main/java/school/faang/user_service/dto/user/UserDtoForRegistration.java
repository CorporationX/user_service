package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserDtoForRegistration(
        long id,

        @NotBlank(message = "username can not be empty")
        String username,

        @NotBlank(message = "password can not be empty")
        String password,

        @NotBlank(message = "email can not be empty")
        String email,

        @Min(value = 1, message = "countryId can not be less than 1")
        long countryId
){}
