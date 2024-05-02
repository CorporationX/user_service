package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalValidator goalValidator;
    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    Goal goal;
    Goal goal2;
    Goal goal3;
    Goal goal4;
    User user;
    GoalDto goalDto;
    GoalFilterDto goalFilterDto;
    List<Goal> subtasks;
    Skill skill1;

    @BeforeEach
    void init() {
    }

    @Test
    @DisplayName("Test successfully save skill and goal for user")
    void testSuccessfullySaveIsActiveSkillAndSaveGoal() {
        user = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        goal = Goal.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goalDto = GoalDto.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

        goalServiceImpl.createGoal(user.getId(), goal);

        InOrder inOrder = inOrder(userRepository, goalValidator, skillService, goalRepository, goalMapper);
        verify(userRepository, times(1)).findById(1L);
//        verify(goalValidator).validateGoalCreation(user, goal, 3);
        verify(goalValidator).validateGoalCreation(any(User.class), eq(goal), eq(3));
        verify(skillService).saveAll(goal.getSkillsToAchieve());
        verify(goalRepository).save(goal);
        verify(goalMapper).toDto(goal);
    }


    @Test
    @DisplayName("Test successfully update goal")
    void testSuccessfullyUpdateGoal() {
        user = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        goal = Goal.builder()
                .id(1L)
                .title("My ultimate goall")
                .description("Learn Python")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goalDto = GoalDto.builder()
                .id(1L)
                .title("My ultimate goal")
                .description("Learn Java")
                .build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        doNothing().when(goalMapper).update(eq(goalDto), eq(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

        goalServiceImpl.updateGoal(1L, goalDto);

        verify(goalRepository, times(1)).findById(1L);
        verify(goalMapper, times(1)).update(goalDto, goal);
        verify(goalRepository, times(1)).save(goal);
        verify(goalMapper, times(1)).toDto(goal);
    }

    @Test
    @DisplayName("Test successfully delete goal")
    void testSuccessfullyDeleteGoal() {

        goal = Goal.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        goalServiceImpl.deleteGoal(1L);

        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    @DisplayName("Test successfully find subtasks by goal id")
    void testSuccessfullyFindSubtasksByGoalId() {

        goal = Goal.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goal2 = Goal.builder()
                .id(2L)
                .title("titles")
                .description("description")
                .parent(goal)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goal3 = Goal.builder()
                .id(3L)
                .title("titles")
                .description("description")
                .parent(goal)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goal4 = Goal.builder()
                .id(4L)
                .title("titles")
                .description("description")
                .parent(goal)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        subtasks = List.of(goal2, goal3, goal4);

        goalDto = GoalDto.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .build();

        when(goalRepository.findByParent(1L)).thenReturn(subtasks.stream());

        goalServiceImpl.findSubtasksByGoalId(1L);

        verify(goalRepository, times(1)).findByParent(1L);
        verify(goalValidator, times(1)).validateFindSubtasks(subtasks, 1L);
        for (Goal subtask : subtasks) {
            verify(goalMapper, times(1)).toDto(subtask);
        }
    }

    @Test
    @DisplayName("Test successfully find goals by user id")
    void testSuccessfullyFindGoalsByUserId() {

        goal = Goal.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goalDto = GoalDto.builder()
                .id(1L)
                .title("titles")
                .description("description")
                .build();

        List<Goal> goals = List.of(
                Goal.builder().id(1L).title("Goal 1").status(GoalStatus.ACTIVE).build(),
                Goal.builder().id(2L).title("Goal 2").status(GoalStatus.COMPLETED).build(),
                Goal.builder().id(3L).title("Goal 3").status(GoalStatus.ACTIVE).build()
        );

        List<GoalDto> goalDtos = goals.stream()
                .map(goalMapper::toDto)
                .collect(Collectors.toList());

        GoalFilterDto goalFilterDto = new GoalFilterDto();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals.stream());
        when(goalValidator.validateGoalsByUserIdAndSort(goalDtos, goalFilterDto)).thenReturn(goalDtos);

        List<GoalDto> result = goalServiceImpl.findGoalsByUserId(1L, goalFilterDto);

        verify(goalRepository, times(1)).findGoalsByUserId(1L);
        verify(goalValidator, times(1)).validateGoalsByUserIdAndSort(goalDtos, goalFilterDto);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test returning an empty goal list when no applicable filters are found")
    void testReturnEmptyListOfGoalsWhenNoApplicableFiltersFound() {
        List<GoalDto> actualGoals = goalServiceImpl.findGoalsByUserId(anyLong(), goalFilterDto);

        assertTrue(actualGoals.isEmpty());
    }

    @Test
    @DisplayName("Test for returning an empty list when there are no subtask")
    public void testReturningEmptyListSubtasksByGoalId() {
        List<GoalDto> actualGoals = goalServiceImpl.findSubtasksByGoalId(anyLong());

        assertTrue(actualGoals.isEmpty());
    }
}
