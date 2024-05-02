package school.faang.user_service.service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipController;
import school.faang.user_service.service.MentorshipService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MentorshipControllerTest {

    @InjectMocks
    private MentorshipController mentorshipController;

    @Mock
    private MentorshipService mentorshipService;

    @Test
    public void testGetMenteesWithNullParameter() {
        assertThrows(NullPointerException.class, () -> mentorshipController.getMentees(null));
    }

    @Test
    public void testGetMentorsWithNullParameter() {
        assertThrows(NullPointerException.class, () -> mentorshipController.getMentors(null));
    }

    @Test
    public void testDeleteMenteeWithNullMenteeParameter() {
        assertThrows(NullPointerException.class, () -> mentorshipController.deleteMentee(null, 1L));
    }

    @Test
    public void testDeleteMenteeWithNullMentorParameter() {
        assertThrows(NullPointerException.class, () -> mentorshipController.deleteMentee(1L, null));
    }

    @Test
    public void testDeleteMentorWithNullMenteeParameter() {
        assertThrows(NullPointerException.class, () -> mentorshipController.deleteMentor(null, 1L));
    }

    @Test
    public void testDeleteMentorWithNullMentorParameter() {
        assertThrows(NullPointerException.class, () -> mentorshipController.deleteMentor(1L, null));
    }

}
