package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipValidatorTest {
    private MentorshipValidator mentorshipValidator = new MentorshipValidator();

    @Test
    void findUserByIdValidate() {
        User user = new User();
        Optional<User> userOptional = Optional.of(user);
        assertEquals(user, mentorshipValidator.findUserByIdValidate(userOptional));

        assertThrows(DataValidationException.class, () -> mentorshipValidator.findUserByIdValidate(Optional.empty()));
    }
}