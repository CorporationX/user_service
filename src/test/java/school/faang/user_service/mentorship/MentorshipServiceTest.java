package school.faang.user_service.mentorship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

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

    @Test
    public void testDeleteMentee_DeletesMentee_WhenMenteeExists() {
        long mentorId = 1L;
        long menteeId = 2L;
        User mentee = new User();
        when(mentorshipRepository.findMenteeByMentorIdAndMenteeId(mentorId, menteeId)).thenReturn(mentee);
        doNothing().when(mentorshipRepository).delete(mentee);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentee_DoesNothing_WhenMenteeDoesNotExist() {
        long mentorId = 1L;
        long menteeId = 2L;
        when(mentorshipRepository.findMenteeByMentorIdAndMenteeId(mentorId, menteeId)).thenReturn(null);
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DeletesMentor_WhenMentorExists() {
        long menteeId = 1L;
        long mentorId = 2L;
        User mentor = new User();
        when(mentorshipRepository.findMentorByMenteeIdAndMentorId(menteeId, mentorId)).thenReturn(mentor);
        doNothing().when(mentorshipRepository).delete(mentor);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }

    @Test
    public void testDeleteMentor_DoesNothing_WhenMentorDoesNotExist() {
        long menteeId = 1L;
        long mentorId = 2L;
        when(mentorshipRepository.findMentorByMenteeIdAndMentorId(menteeId, mentorId)).thenReturn(null);
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}

