package school.faang.user_service.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setMentees(new ArrayList<>());

        mentee = new User();
        mentee.setMentors(new ArrayList<>());
        mentee.setSetGoals(new ArrayList<>());
        mentee.setGoals(new ArrayList<>());
    }

    @Test
    void testStopMentorshipTest_NoGoals() {
        mentee.getMentors().add(mentor);
        mentor.getMentees().add(mentee);
        mentorshipService.stopMentorship(mentor);
        assertTrue(mentee.getMentors().isEmpty());
        assertEquals(0, mentee.getGoals().size());
        verify(mentorshipRepository).save(mentee);
    }

    @Test
    void testStopMentorship_WithGoals() {
        mentee.getMentors().add(mentor);
        mentor.getMentees().add(mentee);
        Goal goal = new Goal();
        mentee.setSetGoals(Arrays.asList(goal));

        mentorshipService.stopMentorship(mentor);

        assertTrue(mentee.getMentors().isEmpty());
        assertEquals(1, mentee.getGoals().size());
        assertTrue(mentee.getGoals().contains(goal));
        assertTrue(mentee.getSetGoals().isEmpty());
        verify(mentorshipRepository).save(mentee);
    }

    @Test
    void testStopMentorship_WithNullSetGoals() {

        mentee.getMentors().add(mentor);
        mentor.getMentees().add(mentee);
        mentee.setSetGoals(new ArrayList<>());
        mentee.setGoals(new ArrayList<>());

        mentorshipService.stopMentorship(mentor);

        assertTrue(mentee.getMentors().isEmpty());
        assertEquals(new ArrayList<>().size(), mentee.getSetGoals().size());
        assertEquals(new ArrayList<>().size(), mentee.getGoals().size());
        verify(mentorshipRepository).save(mentee);
    }
}