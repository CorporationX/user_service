package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @Test
    void testRemoveUserGoalsWhenGoalsExist() {
        long userId = 1L;

        // Mock goals associated with the user
        List<Goal> goals = new ArrayList<>();
        Goal goal1 = mock(Goal.class);
        Goal goal2 = mock(Goal.class);

        // Mock users for the goals
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        // Mock goal IDs
        when(goal1.getId()).thenReturn(100L);

        // Simulate goal1 having the user and being deleted (empty list after removal)
        when(goal1.getUsers()).thenReturn(new ArrayList<>(List.of(user)))
                .thenReturn(new ArrayList<>());  // Empty after removal

        // Simulate goal2 having the user and keeping the list after removal
        when(goal2.getUsers()).thenReturn(new ArrayList<>(List.of(user, mock(User.class))));

        goals.add(goal1);
        goals.add(goal2);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());

        // Call the method
        goalService.removeUserGoals(userId);

        // Verify that goal1 has users removed and is deleted
        verify(goal1, atLeastOnce()).getUsers();
        verify(goalRepository).deleteById(100L);

        // Verify that goal2 has the user removed and setUsers() is called
        verify(goal2, atLeastOnce()).getUsers();
        verify(goal2).setUsers(anyList()); // Expect setUsers to be called with the updated list
        verify(goalRepository, never()).deleteById(200L);  // goal2 is not deleted
    }



    @Test
    void testRemoveUserGoalsWhenGoalsAreEmpty() {
        long userId = 1L;
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(new ArrayList<Goal>().stream());

        goalService.removeUserGoals(userId);

        // Verify no interactions with repository if there are no goals
        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSetUserGoalsToSelf() {
        User mentee = mock(User.class);
        Goal goal1 = mock(Goal.class);
        Goal goal2 = mock(Goal.class);

        List<Goal> goals = List.of(goal1, goal2);
        when(mentee.getGoals()).thenReturn(goals);

        // Call the method
        goalService.setUserGoalsToSelf(mentee);

        // Verify that the goals have the mentor set to mentee
        verify(goal1).setMentor(mentee);
        verify(goal2).setMentor(mentee);
    }
}