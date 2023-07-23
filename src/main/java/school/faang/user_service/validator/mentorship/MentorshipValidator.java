package school.faang.user_service.validator.mentorship;

import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.util.Optional;

public class MentorshipValidator {
    public User findUserByIdValidate(Optional<User> user) {
        return user.orElseThrow(() -> new DataValidationException("User was not found"));
    }
}
