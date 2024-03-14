package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.validator.GoalValidator;


@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @Mock
    private GoalValidator goalValidator;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private GoalController goalController;

    private Long userId;
    private GoalDto goalDto;

    @Test
    void correctTitleTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Test");

        goalController.updateGoal(1L, goalDto);
        Mockito.verify(goalService, Mockito.times(1)).updateGoal(1L, goalDto);
    }


    @Test
    void shouldCreateGoal() {
        userId = 1L;
        goalDto = new GoalDto();
        goalDto.setTitle("Title");
        Mockito.when(userContext.getUserId()).thenReturn(userId);

        goalController.createGoal(goalDto);
        Mockito.verify(goalService, Mockito.times(1)).createGoal(userId, goalDto);
    }

    @Test
    void testShouldDeleteFromService() {
        userId = 1L;
        goalController.deleteGoal(userId);
        Mockito.verify(goalService, Mockito.times(1)).deleteGoal(userId);

    }
}