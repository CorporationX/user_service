package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.entity.contact.PreferredContact;

public record UserDto(
        long id,

        @NotBlank(message = "username can not be empty")
        String username,

        @NotBlank(message = "email can not be empty")
        String email,

        String telegram,
        PreferredContact preferredContact
) {
    public UserDto(long id, String username, String email, PreferredContact preferredContact) {
        this(id, username, email, null, preferredContact);
    }

    public UserDto(long id, String username, String email) {
        this(id, username, email, null, null);
    }
}
