package school.faang.user_service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    private GoalDto goalDto;
    private Long userId;
    private String title;

    @BeforeEach
    void setUp(){
        userId = 1L;
        title = "title";
        goalDto = new GoalDto(userId, title);
        goalDto.setSkills(Arrays.asList("skill1", "skill2", "skill3"));
    }


    @Test
    public void testCreateGoal_Successful() {

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));

        GoalDto result = goalService.createGoal(goalDto, userId);

        assertEquals(result.getId(), userId);
        assertEquals(result.getTitle(), title);
    }

    @Test
    public void testCreateGoal_UnexistingSkills() {

        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, userId));
    }

    @Test
    public void testCreateGoal_TooManyGoals() {

        when(skillRepository.findByTitle(anyString())).thenReturn(Optional.of(new Skill()));
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(5);

        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(goalDto, userId));
    }

    @Test
    public void testUpdateGoal_Successful() {
        String newTitle = "New Title";

        long existingGoalId = 1L;
        Goal existingGoal = new Goal();
        existingGoal.setId(existingGoalId);

        GoalDto goalDto = new GoalDto(userId, title);
        GoalDto newGoalDto = new GoalDto(userId, newTitle);

        when(goalRepository.findById(existingGoalId)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(Mockito.any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalDto result = goalService.updateGoal(newGoalDto, userId);

        assertEquals(newGoalDto, result);
    }

    @Test
    public void testUpdateGoal_GoalNotFound() {
        Long goalId = 123L;
        GoalDto goalDto = new GoalDto(goalId, title);

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(goalDto, userId));
    }

    @Test
    public void testDeleteGoal_Successful() {
        Long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    public void testDeleteGoal_ThrowsException() {
        Long nonExistingGoalId = 100L;

        when(goalRepository.findById(nonExistingGoalId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> goalService.deleteGoal(nonExistingGoalId));
    }
}