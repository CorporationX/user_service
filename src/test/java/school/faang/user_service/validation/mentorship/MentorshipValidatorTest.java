package school.faang.user_service.validation.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MentorshipValidatorTest {
    @Mock
    private MentorshipValidator mentorshipValidator;

    @Test
    void validateMentorMenteeIdsTest_ThrowExceptionForSomeId() {
        long menteeId = 1L;
        long mentorId = 1L;
        Mockito.doThrow(new DataValidationException("mentor and mentor ID are the same"))
                .when(mentorshipValidator).validateMentorMenteeIds(menteeId, mentorId);
        try {
            mentorshipValidator.validateMentorMenteeIds(menteeId, mentorId);
            fail("Expected exception not thrown");
        } catch (DataValidationException e) {
            assertEquals("mentor and mentor ID are the same", e.getMessage());
        }
    }
}
