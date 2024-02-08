package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipValidatorTest {
    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private  MentorshipValidator mentorshipValidator;

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

    @Test
    void validationMentorship_UserIsAlreadyAMentor_Throws() {
        long receiverId = 1L;
        long requesterId = 2L;

        when(mentorshipRepository.existsByMentorIdAndMenteeId(receiverId, requesterId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> mentorshipValidator.validationMentorship(receiverId, requesterId));

        assertEquals("The user is already a mentor of the sender.", dataValidationException.getMessage());
    }

    @Test
    void validationMentorship_UserIsNotAMentor() {
        long receiverId = 1L;
        long requesterId = 2L;

        when(mentorshipRepository.existsByMentorIdAndMenteeId(receiverId, requesterId)).thenReturn(false);

        mentorshipValidator.validationMentorship(receiverId, requesterId);
    }
}

