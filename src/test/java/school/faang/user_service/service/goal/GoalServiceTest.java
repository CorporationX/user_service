package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.GoalOverflowException;
import school.faang.user_service.exceptions.SkillNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillService skillService;
    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);
    @Mock
    private List<GoalFilter> filters;

    @InjectMocks
    private GoalService goalService;

    Goal goal;
    Goal goal2;
    Goal goal3;
    Goal goal4;
    Goal goal5;
    User user;
    User user2;
    User user3;
    User user4;
    GoalDto goalDto4;
    GoalDto goalDto5;
    GoalFilterDto goalFilterDto;
    Stream<Goal> goals;
    Stream<GoalFilter> goalFilters;


    @BeforeEach
    void init() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        Skill skill2 = Skill.builder()
                .id(2L)
                .build();

        Skill skill3 = Skill.builder()
                .id(3L)
                .build();

        goal = Goal.builder()
                .id(1L)
                .title("title")
                .description("descriptional")
                .parent(goal3)
                .skillsToAchieve(Arrays.asList(skill1, skill2, skill3))
                .build();

        goal2 = Goal.builder()
                .id(1L)
                .skillsToAchieve(Arrays.asList(skill1, skill2))
                .build();

        goal3 = Goal.builder()
                .id(1L)
                .title("titles")
                .description("desription")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goal4 = Goal.builder()
                .id(4L)
                .status(GoalStatus.ACTIVE)
                .title("Java")
                .build();

        goal5 = Goal.builder()
                .id(5L)
                .status(GoalStatus.ACTIVE)
                .title("python")
                .build();

        goalDto4 = GoalDto.builder()
                .id(4L)
                .skillIds(Collections.emptyList())
                .status(GoalStatus.ACTIVE)
                .title("Java")
                .build();

        goalDto5 = GoalDto.builder()
                .id(5L)
                .status(GoalStatus.ACTIVE)
                .title("python")
                .build();

        user = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        user2 = User.builder()
                .id(1L)
                .goals(Collections.singletonList(goal2))
                .build();

        user3 = User.builder()
                .id(1L)
                .goals(Arrays.asList(goal, goal2, goal3))
                .build();

        user4 = User.builder()
                .id(4L)
                .goals(Arrays.asList(goal4, goal5))
                .build();

        goalFilterDto = GoalFilterDto.builder()
                .title("Java")
                .build();

        goals = Stream.of(goal4, goal5);

        goalFilters = Stream.of(new GoalStatusFilter(), new GoalTitleFilter());
    }

    @Test
    @DisplayName("Missing target remove test")
    void testDeleteGoalById() {
        goalService.deleteGoal(goal.getId());

        verify(goalRepository).deleteById(goal.getId());
    }

    @Test
    @DisplayName("Test createGoal, for throwing an exception EntityNotFoundException")
    void testCreateGoal() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(anyLong(), goal));
    }

    @Test
    @DisplayName("Test throwing an exception GoalOverflowException")
    public void shouldGoalOverflowException() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));

        assertThrows(GoalOverflowException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    @DisplayName("Test throwing an exception SkillNotFoundException")
    public void shouldSkillNotFoundException() {
        when(skillService.checkActiveSkill(anyLong())).thenReturn(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(SkillNotFoundException.class, () -> goalService.createGoal(user.getId(), goal));
    }

    @Test
    @DisplayName("Test save skill and goal")
    public void shouldSaveIsActiveSkillAndSaveGoal() {
        when(skillService.checkActiveSkill(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        goalService.createGoal(user.getId(), goal3);
        skillService.saveAll(goal2.getSkillsToAchieve());

        verify(goalRepository).create("titles", "desription", goal2.getId());
        verify(skillService).saveAll(goal3.getSkillsToAchieve());
    }

    @Test
    @DisplayName("Test for obtaining a list of goals from a user using a filter")
    public void shouldGetListOfGoalsByUserFromFilters() {
        when(goalRepository.findGoalsByUserId(user4.getId())).thenReturn(goals);
        when(filters.stream()).thenReturn(goalFilters);

        List<GoalDto> actualGoals = goalService.getGoalsByUser(user4.getId(), goalFilterDto);
        List<GoalDto> expectedGoals = Collections.singletonList(goalDto4);

        Assertions.assertTrue(actualGoals.size() == expectedGoals.size() && actualGoals.containsAll(expectedGoals));
    }

    @Test
    @DisplayName("Test returning an empty goal list when no applicable filters are found")
    public void shouldReturnEmptyListOfGoalsWhenNoApplicableFiltersFound() {
        List<GoalDto> actualGoals = goalService.getGoalsByUser(user4.getId(), goalFilterDto);

        Assertions.assertTrue(actualGoals.isEmpty());
    }
}