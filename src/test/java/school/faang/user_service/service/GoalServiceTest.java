package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.EntityFieldsException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Spy
    private GoalMapper goalMapper;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private GoalService goalService;

    @Test
    public void testCreateMaximumGoalsReached() {
        Long userId = 1L;
        int countGoalUser = 3;
        GoalDto goalDto = new GoalDto();
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countGoalUser);

        assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testCreateTitleIsNull() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(null);

        assertThrows(EntityFieldsException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testCreateGoalTitleIsEmpty() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("");

        assertThrows(EntityFieldsException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testCreateGoalTitleIsAllready() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");

        List<Goal> existingGoals = new ArrayList<>();
        Goal goalExist = new Goal();
        goalExist.setTitle("Title");
        existingGoals.add(goalExist);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(existingGoals.stream());


        assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testСreateGoalWithoutAvailableSkills() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");
        goalDto.setSkillIds(List.of(4L));

        Skill skill = new Skill();
        skill.setId(3);
        when(skillRepository.findAll()).thenReturn(List.of(skill));

        assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testCreateSaveGoal() {
        Long userId = 1L;
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("Title");

        Goal goal = new Goal();

        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId()))
                .thenReturn(goal);
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

        GoalDto actualDto = goalService.createGoal(userId, goalDto);
        goalService.createGoal(userId, goalDto);

        assertEquals(goalDto, actualDto);
    }
}
