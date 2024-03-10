package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipControllerTest {
    @InjectMocks
    private MentorshipController mentorshipController;
    @Mock
    private MentorshipService mentorshipService;
    long idTrue;
    long idFalse;
    long menteeId;
    long mentorId;

    @BeforeEach
    public void init() {
        idTrue = 1;
        idFalse = 0;
        menteeId = 1;
        mentorId = 2;
    }

    @Test
    void testGetMenteesSuccessful() {
        List<UserDto> mentees = List.of(new UserDto(), new UserDto());
        when(mentorshipService.getMentees(idTrue)).thenReturn(mentees);
        List<UserDto> result = mentorshipController.getMentees(idTrue);

        assertNotNull(result);
        assertEquals(mentees.size(), result.size());
        assertTrue(mentees.containsAll(result));
        verify(mentorshipService).getMentees(idTrue);
    }

    @Test
    void testGetMentorsSuccessful() {
        List<UserDto> mentors = List.of(new UserDto(), new UserDto());
        when(mentorshipService.getMentors(idTrue)).thenReturn(mentors);
        List<UserDto> result = mentorshipController.getMentors(idTrue);

        assertNotNull(result);
        assertEquals(mentors.size(), result.size());
        assertTrue(mentors.containsAll(result));
        verify(mentorshipService).getMentors(idTrue);
    }

    @Test
    void testDeleteMenteeSuccessful() {
        mentorshipController.deleteMentee(menteeId, mentorId);
        verify(mentorshipService).deleteMentee(menteeId, mentorId);
    }

    @Test
    void testDeleteMentorSuccessful() {
        mentorshipController.deleteMentor(menteeId, mentorId);
        verify(mentorshipService).deleteMentor(menteeId, mentorId);
    }

}