package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {
    @InjectMocks
    private MentorshipController mentorshipController;
    @Mock
    private MentorshipService mentorshipService;
    long idTrue = 1;
    long idFalse = 0;

    @Test
    void testGetMenteesSuccessful() {
        List<UserDto> mentees = List.of(new UserDto(), new UserDto());
        when(mentorshipService.getMentees(idTrue)).thenReturn(mentees);
        List<UserDto> result = mentorshipController.getMentees(idTrue);

        assertNotNull(result);
        assertEquals(mentees.size(), result.size());
        assertIterableEquals(mentees, result);
        verify(mentorshipService).getMentees(idTrue);
    }

    @Test
    void testGetMenteesException() {
        assertThrows(IllegalArgumentException.class, () -> mentorshipController.getMentees(idFalse));
    }

    @Test
    void testGetMentorsSuccessful() {
        List<UserDto> mentors = List.of(new UserDto(), new UserDto());
        when(mentorshipService.getMentors(idTrue)).thenReturn(mentors);
        List<UserDto> result = mentorshipController.getMentors(idTrue);

        assertNotNull(result);
        assertEquals(mentors.size(), result.size());
        assertIterableEquals(mentors, result);
        verify(mentorshipService).getMentors(idTrue);
    }

    @Test
    void testGetMentorsException() {
        assertThrows(IllegalArgumentException.class, () -> mentorshipController.getMentors(idFalse));
    }

    @Test
    void testDeleteMenteeSuccessful() {
        long menteeId = 1;
        long mentorId = 2;
        mentorshipController.deleteMentee(menteeId, mentorId);
        verify(mentorshipService).deleteMentee(menteeId, mentorId);
    }

    @Test
    void testDeleteMentorSuccessful() {
        long menteeId = 1;
        long mentorId = 2;
        mentorshipController.deleteMentor(menteeId, mentorId);
        verify(mentorshipService).deleteMentor(menteeId, mentorId);
    }

    @Test
    void testIsIdValid() {
        assertTrue(mentorshipController.isIdValid(idTrue));
        assertFalse(mentorshipController.isIdValid(idFalse));
    }
}