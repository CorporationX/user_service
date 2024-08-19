package school.faang.user_service.service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private MentorshipService mentorService;

    private User user;
    private User mentee;
    private List<User> mentees;
    private List<User> mentors;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).build();
        mentee = User.builder().id(2L).build();
        mentors = new ArrayList<>();
        mentors.add(user);
        mentees = new ArrayList<>();
        mentees.add(mentee);
        user.setMentees(mentees);
        mentee.setMentors(mentors);
    }

    @Test
    public void testRemoveMenteeFromNonexistentUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserValidationException.class, () -> mentorService.removeMenteeFromUser(1L));
    }

    @Test
    public void testRemoveMenteeFromUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
        mentorService.removeMenteeFromUser(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Assertions.assertTrue(mentee.getMentors().isEmpty());
    }

    @Test
    public void testRemoveMenteeGoalsForNonexistentUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserValidationException.class, () -> mentorService.removeMenteeGoals(1L));
    }

    @Test
    public void testRemoveMenteeGoals() {
        Goal goal = Goal.builder().id(1L).mentor(user).build();
        List<Goal> goals = new ArrayList<>();
        goals.add(goal);
        mentee.setSetGoals(goals);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
        Mockito.when(goalRepository.save(Mockito.any(Goal.class))).thenReturn(null);
        mentorService.removeMenteeGoals(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Assertions.assertEquals(goal.getMentor(), mentee);
    }
}
