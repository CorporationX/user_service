package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.mentorship.MentorshipService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Mock
    private MentorshipService mentorshipService;
    @InjectMocks
    private MentorshipController mentorshipController;

    private final long CORRECT_USER_ID = 1L;

    @Test
    void testGetMentees() {
        mentorshipController.getMentees(CORRECT_USER_ID);
        verify(mentorshipService).getMentees(CORRECT_USER_ID);
    }

    @Test
    void testGetMentors() {
        mentorshipController.getMentors(CORRECT_USER_ID);
        verify(mentorshipService).getMentors(CORRECT_USER_ID);
    }

    @Test
    void testDeleteMentees() {
        mentorshipController.deleteMentee(CORRECT_USER_ID, CORRECT_USER_ID);
        verify(mentorshipService).deleteMentee(CORRECT_USER_ID, CORRECT_USER_ID);
    }

    @Test
    void testDeleteMentors() {
        mentorshipController.deleteMentor(CORRECT_USER_ID, CORRECT_USER_ID);
        verify(mentorshipService).deleteMentor(CORRECT_USER_ID, CORRECT_USER_ID);
    }
}
