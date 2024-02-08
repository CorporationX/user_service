/*
package school.faang.user_service.controller.mentorship;

import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;
import school.faang.user_service.controller.MentorshipController;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.validator.MentorshipValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipValidator mentorshipValidator;
    @InjectMocks
    private MentorshipController mentorshipController;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testAnyMethod_idIsLessThenOne_throwsValidationException () {
        long badMentorId = -2L;
        //long badMenteeId = -3L;
        assertThrowsValidationException(
                () -> mentorshipController.getMentors(badMentorId)
        );
        assertThrowsValidationException(
                () -> mentorshipController.getMentees(badMenteeId)
        );
        assertThrowsValidationException(
                () -> mentorshipController.deleteMentor(badMenteeId, badMentorId)
        );
        assertThrowsValidationException(
                () -> mentorshipController.deleteMentee(badMentorId, badMenteeId)
        );
        Mockito.verify(mentorshipValidator, Mockito.never()).validateMentorshipIds(Mockito.any(), Mockito.any());
        Mockito.verify(mentorshipService, Mockito.never()).deleteMentee(Mockito.any(), Mockito.any());
        Mockito.verify(mentorshipService, Mockito.never()).deleteMentor(Mockito.any(), Mockito.any());
    }

    private void assertThrowsValidationException (Executable method) {
        assertThrows(
                MethodArgumentNotValidException.class,
                method
        );
    }
}
*/
