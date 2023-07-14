package school.faang.user_service.util.mentorshipRequest.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.util.mentorshipRequest.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.mentorshipRequest.exception.TimeHasNotPassedException;
import school.faang.user_service.util.mentorshipRequest.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

public class MentorshipRequestValidatorTest {

    private MentorshipRequestValidator mentorshipRequestValidator;
    private Optional<User> requester;
    private Optional<User> receiver;
    private Optional<MentorshipRequest> mentorshipRequest;

    @BeforeEach
    void setUp() {
        mentorshipRequestValidator = new MentorshipRequestValidator();
        requester = Optional.of(new User());
        receiver = Optional.of(new User());
        mentorshipRequest = Optional.of(new MentorshipRequest());

        mentorshipRequest.get().setCreatedAt(LocalDateTime.now().minusMonths(4));
        requester.get().setId(1);
        receiver.get().setId(2);
    }

    @Test
    void testValidate_InputsAreValid_ShouldBeSuccessful() {
        Assertions.assertDoesNotThrow(
                () -> mentorshipRequestValidator.validate(requester, receiver, mentorshipRequest));
    }

    @Test
    void testValidate_InputsAreInvalidSameIds_ShouldThrowException() {
        requester.get().setId(2);

        Assertions.assertThrows(SameMentorAndMenteeException.class,
                () -> mentorshipRequestValidator.validate(requester, receiver, mentorshipRequest));
    }

    @Test
    void testValidate_InputsAreInvalidEmptyUser_ShouldThrowException() {
        requester = Optional.empty();

        Assertions.assertThrows(UserNotFoundException.class,
                () -> mentorshipRequestValidator.validate(requester, receiver, mentorshipRequest));
    }

    @Test
    void testValidate_InputsAreInvalidTimeHasNotPassed_ShouldThrowException() {
        mentorshipRequest.get().setCreatedAt(LocalDateTime.now().minusMonths(2));

        Assertions.assertThrows(TimeHasNotPassedException.class,
                () -> mentorshipRequestValidator.validate(requester, receiver, mentorshipRequest));
    }
}

