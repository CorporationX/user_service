package school.faang.user_service.service;

import school.faang.user_service.controller.mentorship.MentorshipController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.service.MentorshipService;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Test
    public void testGetMentees() {
        MockitoAnnotations.openMocks(this);
        long userId = 1L;
        when(mentorshipService.getMentees(userId)).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), mentorshipController.getMentees(userId));
    }
}