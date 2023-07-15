package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @Mock
    private MentorshipController mentorshipController;

    @Test
    void testGetMentees() {
        mentorshipController.getMentees(1L);
        Mockito.verify(mentorshipController, Mockito.times(1)).getMentees(1L);
    }
}
