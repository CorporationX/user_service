package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.MentorshipValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MentorshipValidatorTest {
    private MentorshipValidator mentorshipValidator = new MentorshipValidator();

    @Test
    void testValidationForIdsNotEqual() {
        assertThrows(DataValidationException.class,
                () -> mentorshipValidator.validationForIdsNotEqual(1L, 1L));
    }

    @Test
    void validationIfUserIdIsNull() {
        assertThrows(DataValidationException.class,
                () -> mentorshipValidator.validationForNullOrLessThenOneUserId(null));
    }

    @Test
    void validationIfUserIdLessThenOne() {
        assertThrows(DataValidationException.class,
                () -> mentorshipValidator.validationForNullOrLessThenOneUserId(0L));
    }
}
