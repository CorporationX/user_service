package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Spy
    private GoalMapper goalMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    public void testCreateMaximumGoalsReached() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testCreateGoal_TitleGoalExistInDB() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);

        goalDto.setTitle("Title");

        List<Goal> existingGoals = new ArrayList<>();
        Goal goalExist = new Goal();
        goalExist.setTitle("Title");
        existingGoals.add(goalExist);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(existingGoals.stream());


        assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
    }



}
