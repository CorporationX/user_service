package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MentorshipValidatorTest {

    private final MentorshipValidator validator = new MentorshipValidator();

    @Test
    @DisplayName("testValidateMenteeAndMentorIds")
    public void testValidateMenteeAndMentorIds() {
        long menteeId = 1L;
        long mentorId = 1L;

        assertThrows(IllegalArgumentException.class, () -> validator.validateMenteeAndMentorIds(menteeId, mentorId));
    }
}
