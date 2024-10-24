package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.entity.contact.PreferredContact;

public record UserDto(
        long id,

        @NotBlank(message = "username can not be empty")
        String username,

        @NotBlank(message = "email can not be empty")
        String email,

        PreferredContact preference) {
}
