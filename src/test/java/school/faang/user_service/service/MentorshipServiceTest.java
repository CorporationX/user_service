package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Test
    public void testGetMentees() {
        MockitoAnnotations.openMocks(this);
        long userId = 1L;
        when(mentorshipRepository.findMenteesByUserId(userId)).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), mentorshipService.getMentees(userId));
    }
}