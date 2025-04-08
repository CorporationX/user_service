package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.exception.mentorship.InvalidIdException;
import school.faang.user_service.message.mentorship.ExceptionMessage;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of MentorshipControllerTest")
public class MentorshipControllerTest {

    private static final String INVALID_ID_MESSAGE = ExceptionMessage.INVALID_ID.getMessage();
    private static final long INVALID_USER_ID = -1L;
    private static final long FIRST_USER_ID = 1L;
    private static final long SECOND_USER_ID = 2L;

    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private MentorshipController mentorshipController;

    @Test
    @DisplayName("getMentees - invalid user ID")
    public void testGetMenteesWithInvalidId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.getMentees(INVALID_USER_ID)
        );

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        verify(mentorshipService, never()).getMentees(anyLong());
    }

    @Test
    @DisplayName("getMentees - successfully")
    public void testGetMenteesSuccessfully() {
        List<MentorshipUserDto> expectedMentees = List.of(new MentorshipUserDto(), new MentorshipUserDto());
        when(mentorshipService.getMentees(FIRST_USER_ID)).thenReturn(expectedMentees);

        List<MentorshipUserDto> actualMentees = mentorshipController.getMentees(FIRST_USER_ID);

        assertNotNull(actualMentees);
        assertEquals(expectedMentees, actualMentees);
        verify(mentorshipService, times(1)).getMentees(FIRST_USER_ID);
    }

    @Test
    @DisplayName("getMentors - invalid user ID")
    public void testGetMentorsWithInvalidId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.getMentors(INVALID_USER_ID)
        );

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        verify(mentorshipService, never()).getMentees(anyLong());
    }

    @Test
    @DisplayName("getMentors - successfully")
    public void testGetMentorsSuccessfully() {
        List<MentorshipUserDto> expectedMentors = List.of(new MentorshipUserDto(), new MentorshipUserDto());
        when(mentorshipService.getMentors(FIRST_USER_ID)).thenReturn(expectedMentors);

        List<MentorshipUserDto> actualMentors = mentorshipController.getMentors(FIRST_USER_ID);

        assertNotNull(actualMentors);
        assertEquals(expectedMentors, actualMentors);
        verify(mentorshipService, times(1)).getMentors(FIRST_USER_ID);
    }

    @Test
    @DisplayName("deleteMentee - invalid mentee ID and valid mentor ID")
    public void testDeleteMenteeWithInvalidMenteeId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentee(INVALID_USER_ID, SECOND_USER_ID)
        );

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        verify(mentorshipService, never()).deleteMentee(anyLong(), anyLong());
    }

    @Test
    @DisplayName("deleteMentee - invalid mentor ID and valid mentee ID")
    public void testDeleteMenteeWithInvalidMentorId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentee(FIRST_USER_ID, INVALID_USER_ID)
        );

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        verify(mentorshipService, never()).deleteMentee(anyLong(), anyLong());
    }

    @Test
    @DisplayName("deleteMentee - successfully")
    public void testDeleteMenteeSuccessfully() {
        mentorshipController.deleteMentee(FIRST_USER_ID, SECOND_USER_ID);

        verify(mentorshipService, times(1)).deleteMentee(FIRST_USER_ID, SECOND_USER_ID);
    }

    @Test
    @DisplayName("deleteMentor - invalid mentee ID and valid mentor ID")
    public void testDeleteMentorWithInvalidMenteeId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentor(INVALID_USER_ID, SECOND_USER_ID)
        );

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        verify(mentorshipService, never()).deleteMentor(anyLong(), anyLong());
    }

    @Test
    @DisplayName("deleteMentor - invalid mentor ID and valid mentee ID")
    public void testDeleteMentorWithInvalidMentorId() {
        Exception exception = assertThrows(
                InvalidIdException.class,
                () -> mentorshipController.deleteMentor(FIRST_USER_ID, INVALID_USER_ID)
        );

        assertEquals(INVALID_ID_MESSAGE, exception.getMessage());
        verify(mentorshipService, never()).deleteMentor(anyLong(), anyLong());
    }

    @Test
    @DisplayName("deleteMentor - successfully")
    public void testDeleteMentorSuccessfully() {
        mentorshipController.deleteMentor(FIRST_USER_ID, SECOND_USER_ID);

        verify(mentorshipService, times(1)).deleteMentor(FIRST_USER_ID, SECOND_USER_ID);
    }
}
