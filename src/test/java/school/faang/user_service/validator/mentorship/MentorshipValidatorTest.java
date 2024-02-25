package school.faang.user_service.validator.mentorship;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipValidatorTest {
    @InjectMocks
    MentorshipValidator mentorshipValidator;

    @Test
    public void testValidateMentorshipIds_idsAreEquals_throwsDataValidationException() {
        long firstUserId = 1L;
        long secondUserId = 1L;
        assertThrows(
                ValidationException.class,
                () -> mentorshipValidator.validateMentorshipIds(firstUserId, secondUserId)
        );
    }

    @Test
    public void testValidateMentorshipIds_idsAreUnequals_nothingHappens() {
        long firstUserId = 1L;
        long secondUserId = 2L;
        mentorshipValidator.validateMentorshipIds(firstUserId, secondUserId);
    }
}