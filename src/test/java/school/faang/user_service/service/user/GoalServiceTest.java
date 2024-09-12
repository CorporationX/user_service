package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalService goalService;

    private Goal goal;
    private List<User> mentees;

    @BeforeEach
    public void setUp() {
        goal = new Goal();

        List<Goal> children = new ArrayList<>(List.of(
                new Goal(),
                new Goal()
        ));

        goal.setChildrenGoals(children);
    }

    @Test
    public void deleteGoalAndUnlinkChildrenSuccess() {
        Goal child1 = goal.getChildrenGoals().get(0);
        Goal child2 = goal.getChildrenGoals().get(1);

        goalService.deleteGoalAndUnlinkChildren(goal);

        verify(goalRepository, times(2)).save(any(Goal.class));
        verify(goalRepository).delete(goal);

        assertNull(child1.getParent());
        assertNull(child2.getParent());
    }
}