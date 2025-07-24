package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

public class RequesterIdNotBeEqualReceiverIdValidatorTest {

    private RequesterIdNotBeEqualReceiverIdValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequesterIdNotBeEqualReceiverIdValidator();
    }

    @Test
    @DisplayName("validate should throw when requester equals receiver")
    void validate_shouldThrow_whenRequesterEqualsReceiver() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 1L, "test");

        Assertions.assertThrows(ResponseStatusException.class, () -> validator.validate(dto));
    }

    @Test
    @DisplayName("validate should pass when requester not equals receiver")
    void validate_shouldPass_whenRequesterNotEqualsReceiver() {
        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "test");

        Assertions.assertDoesNotThrow(() -> validator.validate(dto));
    }
}