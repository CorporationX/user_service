package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.*;

class GoalServiceTest {

    @Mock
    GoalRepository goalRepository;

    @ExtendWith(MockitoExtension.class)
    @InjectMocks
    GoalService service;

    @Test
    public void updateGoalComletedValidation() {
        Goal goal = new Goal().builder().status(GoalStatus.COMPLETED).id(0L).build();
        GoalDto newGoal = new GoalDto().builder().title("a").status(GoalStatus.COMPLETED).build();
        assertThrows(IllegalArgumentException.class, () -> {
            service.updateGoal(goal.getId(), newGoal);
        });
    }
}