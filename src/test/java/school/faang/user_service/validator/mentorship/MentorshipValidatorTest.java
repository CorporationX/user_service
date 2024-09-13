package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MentorshipValidatorTest {

    private MentorshipValidator validator = new MentorshipValidator();

    @Test
    void testValidationListThrowExceptionWithNull() {
        assertThrows(DataValidationException.class, () -> validator.validateMenteesList(null));
    }
}
