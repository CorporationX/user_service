package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.ExceptionMessages;

import static org.junit.jupiter.api.Assertions.*;

class SelfMentorshipValidatorTest {

    private final SelfMentorshipValidator validator = new SelfMentorshipValidator();

    @Test
    void validate_throws_exception_when_requester_is_the_receiver() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(1L);

        assertThrows(IllegalArgumentException.class, () -> validator.validate(dto), ExceptionMessages.SELF_MENTORSHIP);
    }

    @Test
    void validate_does_nothing_when_requester_is_not_the_receiver() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        assertDoesNotThrow(() -> validator.validate(dto));
    }

}