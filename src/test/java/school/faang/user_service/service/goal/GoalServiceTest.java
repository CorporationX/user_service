package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.ErrorCode;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.TestGoalActiveStatusFilter;
import school.faang.user_service.filter.goal.TestGoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    private static Skill firstSkill;
    private static Skill secondSkill;
    private static GoalDto goalDto;
    private static Goal goalForSkills;
    private static User firstUser;
    private static User secondUser;

    private final GoalFilter goalStatusFilter = new TestGoalActiveStatusFilter();
    private final GoalFilter goalTitleFilter = new TestGoalTitleFilter();

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapperImpl goalMapper;
    private GoalService goalService;

    @BeforeEach
    public void setUp() {
        goalService = new GoalService(goalRepository, skillRepository, goalMapper,
                List.of(goalStatusFilter, goalTitleFilter));
        firstSkill = Skill.builder().id(1L).build();
        secondSkill = Skill.builder().id(2L).build();
        goalDto = GoalDto.builder().id(1L).title("Title")
                .description("description").parentId(1L)
                .skillIds(List.of(1L, 2L)).build();
        goalForSkills = Goal.builder().id(1L).title("Goal Title")
                .skillsToAchieve(new ArrayList<>()).build();
        firstUser = User.builder().id(1L).build();
        secondUser = User.builder().id(2L).build();
    }

    @Test
    public void testNullTitleIsInvalid() {
        GoalDto goalDtoWithNullTitle = GoalDto.builder().title(null).build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDtoWithNullTitle));

        assertEquals(ErrorCode.GOAL_EMPTY_TITLE, exception.getErrorCode());
        assertEquals(ErrorCode.GOAL_EMPTY_TITLE.getDescription(), exception.getErrorCode().getDescription());

    }

    @Test
    public void testNullTitleIsEmpty() {
        GoalDto goalDtoWithEmptyTitle = GoalDto.builder().title("").build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDtoWithEmptyTitle));

        assertEquals(ErrorCode.GOAL_EMPTY_TITLE, exception.getErrorCode());
        assertEquals(ErrorCode.GOAL_EMPTY_TITLE.getDescription(), exception.getErrorCode().getDescription());
    }

    @Test
    public void testMoreThanMaxActiveGoals() {
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(4);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
        assertEquals(ErrorCode.MAX_ACTIVE_GOALS, exception.getErrorCode());
        assertEquals(ErrorCode.MAX_ACTIVE_GOALS.getDescription(), exception.getErrorCode().getDescription());
    }

    @Test
    public void testNonExistingSkills() {
        GoalDto goalDtoWithNoSkillId = GoalDto.builder().title("Title").skillIds(List.of(1L, 2L, 4L)).build();

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillRepository.findAllById(List.of(1L, 2L, 4L))).thenReturn(List.of(firstSkill, secondSkill));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDtoWithNoSkillId));
        assertEquals(ErrorCode.GOAL_NON_EXISTING_SKILLS, exception.getErrorCode());
        assertEquals(ErrorCode.GOAL_NON_EXISTING_SKILLS.getDescription(), exception.getErrorCode().getDescription());
    }

    @Test
    public void testAddSkillsToGoal() {
        Goal createdGoal = Goal.builder().id(1L).title("Title").description("description")
                .parent(null).skillsToAchieve(List.of(firstSkill, secondSkill)).build();

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstSkill, secondSkill));
        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId()))
                .thenReturn(createdGoal);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goalForSkills));
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(
                Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));
        GoalDto result = goalService.createGoal(1L, goalDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Title", result.getTitle());
        assertEquals("description", result.getDescription());
    }

    @Test
    public void testUpdateGoalWithEmptySkills() {
        Goal goal = Goal.builder().id(1L).status(GoalStatus.ACTIVE).skillsToAchieve(List.of()).build();

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstSkill, secondSkill));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        GoalDto result = goalService.updateGoal(1L, goalDto);

        assertNotNull(result);
    }

    @Test
    public void testUpdateGoal() {
        Goal goal = Goal.builder().id(1L).status(GoalStatus.COMPLETED)
                .skillsToAchieve(new ArrayList<>(List.of(firstSkill)))
                .users(List.of(firstUser, secondUser)).build();

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstSkill, secondSkill));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        doAnswer(ans -> {
                    long id = ans.getArgument(0);
                    goal.getSkillsToAchieve().removeIf(s -> firstSkill.getId() == id);
                    return null;
                }).when(goalRepository).removeSkillsFromGoal(anyLong());
        GoalDto result = goalService.updateGoal(1L, goalDto);

        assertNotNull(result);
        assertEquals(2, goal.getSkillsToAchieve().size());
        assertTrue(goal.getSkillsToAchieve().contains(firstSkill));
        assertTrue(goal.getSkillsToAchieve().contains(secondSkill));
    }

    @Test
    public void testDeleteGoal() {
        Goal goal = Goal.builder().id(1L).title("Test Goal").build();

        goalRepository.save(goal);
        goalService.deleteGoal(1L);
        Optional<Goal> deletedGoal = goalRepository.findById(1L);

        assertFalse(deletedGoal.isPresent());
    }

    @Test
    public void testFindSubtasks() {
        Goal subtask1 = Goal.builder().id(2L).title("Subtask 1").build();
        Goal subtask2 = Goal.builder().id(3L).title("Subtask 2").build();
        List<Goal> subtasks = List.of(subtask1, subtask2);
        GoalDto subtaskDto1 = GoalDto.builder().id(2L).title("Subtask 1").build();
        GoalDto subtaskDto2 = GoalDto.builder().id(3L).title("Subtask 2").build();
        List<GoalDto> expectedDtos = List.of(subtaskDto1, subtaskDto2);

        when(goalRepository.findByParent(1L)).thenReturn(subtasks.stream());
        doReturn(subtaskDto1).when(goalMapper).toDto(subtask1);
        doReturn(subtaskDto2).when(goalMapper).toDto(subtask2);
        List<GoalDto> result = goalService.findSubtasksByGoalId(1L);

        assertEquals(expectedDtos, result);
        verify(goalRepository, times(1)).findByParent(1L);
        verify(goalMapper, times(1)).toDto(subtask1);
        verify(goalMapper, times(1)).toDto(subtask2);
    }

    @Test
    public void testGetGoalsByUserIdNoActiveStatus() {
        Goal goal1 = Goal.builder().id(1L).title("FirstGoal").status(GoalStatus.COMPLETED).build();
        Goal goal2 = Goal.builder().id(2L).title("SecondGoal").status(GoalStatus.COMPLETED).build();
        Goal goal3 = Goal.builder().id(3L).title("ThirdGoal").status(GoalStatus.COMPLETED).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetGoalsByUserIdNoSuitableTitle() {
        Goal goal1 = Goal.builder().id(1L).title(null).status(GoalStatus.COMPLETED).build();
        Goal goal2 = Goal.builder().id(2L).title(null).status(GoalStatus.COMPLETED).build();
        Goal goal3 = Goal.builder().id(3L).title(null).status(GoalStatus.COMPLETED).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetGoalsByUserIdTwoActiveStatus() {
        Goal goal1 = Goal.builder().id(1L).title("FirstGoal").status(GoalStatus.ACTIVE).build();
        Goal goal2 = Goal.builder().id(2L).title("SecondGoal").status(GoalStatus.ACTIVE).build();
        Goal goal3 = Goal.builder().id(3L).title("ThirdGoal").status(GoalStatus.COMPLETED).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertEquals(1, result.size());
    }

    @Test
    public void testGetGoalsByUserId() {
        Goal goal1 = Goal.builder().id(1L).title("FirstGoal").status(GoalStatus.ACTIVE).build();
        Goal goal2 = Goal.builder().id(2L).title("SecondGoal").status(GoalStatus.COMPLETED).build();
        Goal goal3 = Goal.builder().id(3L).title("ThirdGoal").status(GoalStatus.ACTIVE).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertEquals(1, result.size());
    }
}

