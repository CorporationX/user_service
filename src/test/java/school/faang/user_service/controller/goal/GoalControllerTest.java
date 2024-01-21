package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.goal.GoalValidator;


@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;
    @Mock
    private GoalValidator goalValidator;

    @InjectMocks
    private GoalController goalController;

    private Long userId;
    private GoalDto goalDto;


    @Test
    void shouldCreateGoal() {
        userId = 1L;
        goalDto = new GoalDto();
        goalDto.setTitle("Title");

        goalController.createGoal(userId, goalDto);
        Mockito.verify(goalService, Mockito.times(1)).createGoal(userId, goalDto);
    }
}