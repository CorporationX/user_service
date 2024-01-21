package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
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


    @Test
    void correctTitleTest(){
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Test");
        goalController.updateGoal(1L, goalDto);
        Mockito.verify(goalService, Mockito.times(1)).updateGoal(1L, goalDto);
    }
}