package school.faang.user_service.validator.mentorship;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipValidatorTest {
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private MentorshipValidator mentorshipValidator;
    private static final long CORRECT_ID = 1L;
    private static final long INCORRECT_ID = 0L;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setMentees(List.of(new User()));
        Mockito.when(userValidator.validateUser(CORRECT_ID))
                .thenReturn(user);

        Mockito.when(userValidator.validateUser(INCORRECT_ID))
                .thenThrow(new RuntimeException("Invalid user id: " + INCORRECT_ID));
    }

    @Test
    void validateToDeleteMentee_shouldInvokeValidateUserMethod() {
        mentorshipValidator.validateToDeleteMentee(CORRECT_ID, CORRECT_ID);
        Mockito.verify(userValidator, Mockito.times(2)).validateUser(CORRECT_ID);
    }

    @Test
    void validateToDeleteMentee_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> mentorshipValidator.validateToDeleteMentee(INCORRECT_ID, CORRECT_ID),
                "Invalid user id: " + INCORRECT_ID);

        assertThrows(RuntimeException.class,
                () -> mentorshipValidator.validateToDeleteMentee(CORRECT_ID, INCORRECT_ID),
                "Invalid user id: " + INCORRECT_ID);

        assertThrows(RuntimeException.class,
                () -> mentorshipValidator.validateToDeleteMentee(CORRECT_ID, CORRECT_ID),
                "Mentor with id: " + CORRECT_ID + " does not have mentee with id: " + CORRECT_ID);
    }
}