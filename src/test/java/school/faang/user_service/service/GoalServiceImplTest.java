package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.validator.GoalValidator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {
    private static final int GOALS_PER_USER = 3;

    private Long userId;
    private Long goalId;
    private GoalDto validGoalDto;
    private GoalDto emptyTitleGoalDto;

    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private List<GoalFilter> goalFilters;

    @Spy
    private GoalMapper goalMapper;

    @Spy
    private GoalValidator goalValidator;

    @BeforeEach
    void setUp() {
        userId = 1L;
        goalId = 2L;
        emptyTitleGoalDto = prepareData("  ");
        validGoalDto = prepareData("some title");
    }

    private GoalDto prepareData(String title) {
        GoalDto goalDto = new GoalDto();
        goalDto.setId(1L);
        goalDto.setTitle(title);
        return goalDto;
    }

    @Test
    public void testCreateWithEmptyTitle() {
        DataValidationException thrown = assertThrows(DataValidationException.class, () ->
                goalServiceImpl.createGoal(userId, emptyTitleGoalDto));

        assertEquals("Title cannot be empty", thrown.getMessage());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testCreateWithUserHavingToManyGoals() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(GOALS_PER_USER);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> goalServiceImpl.createGoal(userId, validGoalDto));

        assertEquals("There cannot be more than " + GOALS_PER_USER + " active goals per user", thrown.getMessage());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testCreateWithNotExistingSkills() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(GOALS_PER_USER-1);
        validGoalDto.setSkillIds(List.of(1L, 2L));
        when(skillRepository.countExisting(validGoalDto.getSkillIds())).thenReturn(validGoalDto.getSkillIds().size()-1);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> goalServiceImpl.createGoal(userId, validGoalDto));

        assertEquals("Cannot create goal with non-existent skills", thrown.getMessage());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    public void testGettingGoalsByUserWithNoGoals() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();

        assertEquals(goalServiceImpl.getGoalsByUser(userId, goalFilterDto), Collections.emptyList());
    }

    @Test
    public void testFindingSubtasksWithNoGoals() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();

        assertEquals(goalServiceImpl.findSubtasksByGoalId(goalId, goalFilterDto), Collections.emptyList());
    }
}
