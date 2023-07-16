package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.service.mentorship.MentorshipService;

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
        Mockito.verify(mentorshipService, Mockito.times(1)).getMentees(CORRECT_USER_ID);
    }
}
