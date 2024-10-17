package school.faang.user_service.model.dto.mentorship;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

public record MentorshipDto(
        @Null(message = "Mentorship ID must be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long id,

        @NotBlank(message = "Username must not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        String username,

        @NotBlank(message = "Email must not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        @Email
        String email,

        @NotBlank(message = "Phone number must not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        String phone,

        @NotBlank(message = "About Me must not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        String aboutMe
) {
}
