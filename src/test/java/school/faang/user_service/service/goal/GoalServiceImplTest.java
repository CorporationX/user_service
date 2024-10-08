package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.impl.goal.GoalServiceImpl;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    private List<Goal> goals;

    @BeforeEach
    void setup(){
        goals = List.of(new Goal());
    }

    @Test
    void testRemoveGoals(){
        goalService.removeGoals(goals);

        verify(goalRepository).deleteAll(goals);
    }
}
