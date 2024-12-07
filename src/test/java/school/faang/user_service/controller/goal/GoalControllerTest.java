package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    @Mock
    private GoalService goalService;
    @InjectMocks
    private GoalController goalController;

    @Test
    public void testCreate() {
        GoalDto anyGoalDto = Mockito.any(GoalDto.class);
        long anuLong = Mockito.anyLong();

        goalController.create(anuLong, anyGoalDto);

        Mockito.verify(goalService, Mockito.times(1)).create(anuLong, anyGoalDto);
    }

    @Test
    public void testUpdate() {
        GoalDto anyGoalDto = Mockito.any(GoalDto.class);
        long anuLong = Mockito.anyLong();

        goalController.update(anuLong, anyGoalDto);

        Mockito.verify(goalService, Mockito.times(1)).update(anuLong, anyGoalDto);
    }

    @Test
    public void testDelete() {
        long anuLong = Mockito.anyLong();

        goalController.delete(anuLong);

        Mockito.verify(goalService, Mockito.times(1)).delete(anuLong);
    }
}
