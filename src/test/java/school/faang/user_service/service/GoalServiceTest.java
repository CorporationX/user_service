package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.User;
import school.faang.user_service.model.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    private Goal goalWithUsers;
    private Goal goalWithoutUsers;

    @BeforeEach
    void setUp() {

        goalWithUsers = new Goal();
        goalWithUsers.setId(1L);
        goalWithUsers.setUsers(List.of(new User()));

        goalWithoutUsers = new Goal();
        goalWithoutUsers.setId(2L);
        goalWithoutUsers.setUsers(List.of());
    }

    @Test
    void testGetGoalByIdReturnsGoalWhenFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goalWithoutUsers));

        Goal result = goalService.getGoalById(1L);

        assertNotNull(result);

    }

    @Test
    void getGoalById_throwsExceptionWhenNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.getGoalById(1L));

    }

    @Test
    void testRemoveGoalsWithoutExecutingUsers_removesEmptyGoals() {
        List<Goal> goals = List.of(goalWithUsers, goalWithoutUsers);

        goalService.removeGoalsWithoutExecutingUsers(goals);

        verify(goalRepository).deleteById(goalWithoutUsers.getId());
        verify(goalRepository, never()).deleteById(goalWithUsers.getId());
    }

    @Test
    void testRemoveGoalsWithoutExecutingUsers_doesNothingWhenAllGoalsHaveUsers() {
        List<Goal> goals = List.of(goalWithUsers);

        goalService.removeGoalsWithoutExecutingUsers(goals);

        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    void removeGoalsWithoutExecutingUsersRemovesEmptyGoals() {
        List<Goal> goals = List.of(goalWithUsers, goalWithoutUsers);

        goalService.removeGoalsWithoutExecutingUsers(goals);

        verify(goalRepository).deleteById(goalWithoutUsers.getId());
        verify(goalRepository, never()).deleteById(goalWithUsers.getId());

    }

    @Test
    void testRemoveGoalsWithoutExecutingUsers_handlesEmptyGoalList() {
        List<Goal> goals = List.of();

        goalService.removeGoalsWithoutExecutingUsers(goals);

        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    void mapListIdsToGoals_returnsCorrectGoals() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goalWithUsers));
        when(goalRepository.findById(2L)).thenReturn(Optional.of(goalWithoutUsers));
        List<Long> goalIds = List.of(1L, 2L);

        List<Goal> result = goalService.mapListIdsToGoals(goalIds);

        assertNotNull(result);
        assertEquals(2, result.size(), "List should contain two goals");
        assertEquals(goalWithUsers.getId(), result.get(0).getId(), "First goal ID should be 1");
        assertEquals(goalWithoutUsers.getId(), result.get(1).getId(), "Second goal ID should be 2");
    }

    @Test
    void mapListIdsToGoals_returnsEmptyListForEmptyInput() {
        List<Long> goalIds = List.of();
        List<Goal> result = goalService.mapListIdsToGoals(goalIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}