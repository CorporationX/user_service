package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Mock
    private MentorshipController mentorshipController;

    @Test
    void testGetMentees() {
        mentorshipController.getMentees(1L);
        verify(mentorshipController, times(1)).getMentees(1L);
    }

    @Test
    void getMentors() {
        mentorshipController.getMentors(1L);
        verify(mentorshipController, times(1)).getMentors(1L);
    }
}
