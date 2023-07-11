package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.goal.GoalService;

import static org.junit.jupiter.api.Assertions.*;

class GoalControllerTest {

    @Mock
    GoalService service;

    @ExtendWith(MockitoExtension.class)
    @InjectMocks
    GoalController controller;

    @Test
    public void getGoalsByUserWithIDLessThanOneTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.getGoalsByUser(0L, null);
        });
    }

    @Test
    public void getGoalsByUserIdWithNoFilterTest() {
        assertEquals(
                controller.getGoalsByUser(1L, null),
                service.findGoalsByUserId(1L));
    }

    @Test
    public void getGoalsByUserIdDataWasCollectedTest() {
        controller.getGoalsByUser(1L, null);
        Mockito.verify(service).findGoalsByUserId(1L);
    }

}