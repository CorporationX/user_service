package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.service.mentorship.MentorshipService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MentorshipControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MentorshipControllerTest.class);

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteMentee_DoesNothing_WhenMenteeDoesNotExist() {
        long mentorId = 1L;
        long menteeId = 2L;
        doThrow(new RuntimeException("Mentee doesn't exist")).when(mentorshipService).deleteMentee(menteeId, mentorId);
        assertThrows(RuntimeException.class, () -> mentorshipController.deleteMentee(menteeId, mentorId));
        verify(mentorshipService, times(1)).deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentee_DeletesMentee_WhenMenteeExists() {
        long mentorId = 1L;
        long menteeId = 2L;
        doNothing().when(mentorshipService).deleteMentee(menteeId, mentorId);
        mentorshipController.deleteMentee(menteeId, mentorId);
        verify(mentorshipService, times(1)).deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DoesNothing_WhenMentorDoesNotExist() {
        long menteeId = 1L;
        long mentorId = 2L;
        doNothing().when(mentorshipService).deleteMentor(menteeId, mentorId);
        mentorshipController.deleteMentor(menteeId, mentorId);
        verify(mentorshipService, times(1)).deleteMentor(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DeletesMentor_WhenMentorExists() {
        long menteeId = 1L;
        long mentorId = 2L;
        doNothing().when(mentorshipService).deleteMentor(menteeId, mentorId);
        mentorshipController.deleteMentor(menteeId, mentorId);
        verify(mentorshipService, times(1)).deleteMentor(menteeId, mentorId);
    }
}