package school.faang.user_service.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.repository.GoalRepository;
import school.faang.user_service.repository.MentorshipRepository;
import school.faang.user_service.service.impl.MentorshipServiceImpl;

import java.util.ArrayList;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    public void setUp() {
        mentor = new User();
        mentor.setId(1L);
        mentor.setMentees(new ArrayList<>());
        mentor.setMentors(new ArrayList<>());

        mentee = new User();
        mentee.setId(2L);
        mentee.setGoals(new ArrayList<>());
        mentee.setMentors(new ArrayList<>());
        mentee.setSetGoals(new ArrayList<>());
    }

    @Test
    public void testStopMentorship_NoMentees() {
        mentor.setMentees(Collections.emptyList());
        mentorshipService.stopMentorship(mentor);
        verify(mentorshipRepository, never()).save(any());
        assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    public void testStopMentorship_WithMentees() {
        mentee.setMentors(new ArrayList<>(Collections.singletonList(mentor)));
        mentor.getMentees().add(mentee);

        Goal goal = new Goal();
        goal.setMentor(mentor);
        mentee.setSetGoals(new ArrayList<>(Collections.singletonList(goal)));
        mentorshipService.stopMentorship(mentor);
        verify(mentorshipRepository, times(1)).save(mentee);
        assertTrue(mentee.getMentors().isEmpty());
        assertEquals(1, mentee.getGoals().size());
        assertEquals(mentee, goal.getMentor());
    }

    @Test
    public void testTransferGoals() {
        mentee.setMentors(new ArrayList<>(Collections.singletonList(mentor)));
        mentor.getMentees().add(mentee);

        Goal goal = new Goal();
        goal.setMentor(mentor);
        mentee.setSetGoals(new ArrayList<>(Collections.singletonList(goal)));
        mentorshipService.stopMentorship(mentor);
        assertEquals(1, mentee.getGoals().size());
        assertEquals(mentee, goal.getMentor());
    }

    @Test
    public void testStopMentorship_NullMentees() {
        mentor.setMentees(null);
        mentorshipService.stopMentorship(mentor);
        verify(mentorshipRepository, never()).save(any());
    }

    @Test
    public void testTransferGoals_NoGoals() {
        mentee.setMentors(new ArrayList<>(Collections.singletonList(mentor)));
        mentor.getMentees().add(mentee);
        mentorshipService.stopMentorship(mentor);
        verify(goalRepository, never()).saveAll(any());
        assertTrue(mentee.getGoals().isEmpty());
    }
}