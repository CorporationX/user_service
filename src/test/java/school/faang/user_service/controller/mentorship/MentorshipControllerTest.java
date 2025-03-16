package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    private static final String INVALID_ID_MESSAGE = "Invalid ID: ID not be less than 1";
    private static final long INVALID_USER_ID = -1L;
    private static final long FIRST_USER_ID = 1L;
    private static final long SECOND_USER_ID = 2L;
    private static final long THIRD_USER_ID = 3L;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    public void testGetMenteesWithInvalidId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.getMentees(INVALID_USER_ID)
        );
        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testGetMenteesSuccessfully() {
        long userId = FIRST_USER_ID;
        List<Long> expectedMentees = List.of(SECOND_USER_ID, THIRD_USER_ID);
        when(mentorshipService.getMentees(userId)).thenReturn(expectedMentees);

        List<Long> actualMentees = mentorshipController.getMentees(userId);

        assertEquals(expectedMentees, actualMentees);
    }

    @Test
    public void testGetMentorsWithInvalidId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.getMentors(INVALID_USER_ID)
        );
        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testGetMentorsSuccessfully() {
        long userId = FIRST_USER_ID;
        List<Long> expectedMentors = List.of(SECOND_USER_ID, THIRD_USER_ID);
        when(mentorshipService.getMentors(userId)).thenReturn(expectedMentors);

        List<Long> actualMentors = mentorshipController.getMentors(userId);

        assertEquals(expectedMentors, actualMentors);
    }

    @Test
    public void testDeleteMenteeWithInvalidMenteeId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentee(INVALID_USER_ID, SECOND_USER_ID)
        );
        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testDeleteMenteeWithInvalidMentorId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentee(FIRST_USER_ID, INVALID_USER_ID)
        );
        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testDeleteMenteeSuccessfully() {
        mentorshipController.deleteMentee(FIRST_USER_ID, SECOND_USER_ID);

        verify(mentorshipService, times(1)).deleteMentee(FIRST_USER_ID, SECOND_USER_ID);
    }

    @Test
    public void testDeleteMentorWithInvalidMenteeId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentor(INVALID_USER_ID, SECOND_USER_ID)
        );
        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testDeleteMentorWithInvalidMentorId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentor(FIRST_USER_ID, INVALID_USER_ID)
        );
        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
    }

    @Test
    public void testDeleteMentorSuccessfully() {
        mentorshipController.deleteMentor(FIRST_USER_ID, SECOND_USER_ID);

        verify(mentorshipService, times(1)).deleteMentor(FIRST_USER_ID, SECOND_USER_ID);
    }
}
