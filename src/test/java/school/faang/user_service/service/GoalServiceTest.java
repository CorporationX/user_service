package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.filter.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    private Long userId;
    private Long goalId;
    private GoalDto validGoalDto;
    private GoalDto emptyTitleGoalDto;

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private List<GoalFilter> goalFilters;

    @Spy
    private GoalMapper goalMapper;

    @Spy
    private GoalValidator goalValidator = new GoalValidator(goalRepository, skillRepository);

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
        ValidationException thrown = assertThrows(ValidationException.class, () ->
                goalService.createGoal(userId, emptyTitleGoalDto));

        assertEquals("Title cannot be empty", thrown.getMessage());
        verify(goalRepository, never()).create(
                anyString(),
                anyString(),
                anyLong(),
                any()
        );
    }

    @Test
    public void testCreateWithUserHavingToManyGoals() {
        doThrow(ValidationException.class).when(goalValidator).validateGoalsPerUser(userId);

        assertThrows(ValidationException.class, () ->
                goalService.createGoal(userId, validGoalDto));
        verify(goalRepository, never()).create(
                anyString(),
                anyString(),
                anyLong(),
                any()
        );
    }

    @Test
    public void testCreateWithNotExistingSkills() {
        doNothing().when(goalValidator).validateGoalsPerUser(userId);
        doThrow(ValidationException.class).when(goalValidator).validateGoalSkills(validGoalDto.getSkillIds());

        assertThrows(ValidationException.class, () -> goalService.createGoal(userId, validGoalDto));
        verify(goalRepository, never()).create(
                anyString(),
                anyString(),
                anyLong(),
                any()
        );
    }

    @Test
    public void testGettingGoalsByUserWithNoGoals() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();

        assertEquals(goalService.getGoalsByUser(userId, goalFilterDto), Collections.emptyList());
    }

    @Test
    public void testFindingSubtasksWithNoGoals() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();

        assertEquals(goalService.findSubtasksByGoalId(goalId, goalFilterDto), Collections.emptyList());
    }
}
