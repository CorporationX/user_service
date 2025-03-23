package school.faang.user_service.service.goal;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
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
    }

    @Test
    public void testNullTitleIsInvalid() {
        GoalDto goalDto = GoalDto.builder().title(null).build();
        Assert.assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
    }

    @Test
    public void testNullTitleIsEmpty() {
        GoalDto goalDto = GoalDto.builder().title("").build();
        Assert.assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
    }

    @Test
    public void testMoreThanMaxActiveGoals() {
        GoalDto goalDto = GoalDto.builder().title("Title").build();
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(4);
        Assert.assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
    }

    @Test
    public void testNonExistingSkills() {
        GoalDto goalDto = GoalDto.builder().title("Title").skillIds(List.of(1L, 2L, 4L)).build();
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillRepository.findAllById(List.of(1L, 2L, 4L))).thenReturn(List.of(skill1, skill2));
        Assert.assertThrows(ValidationException.class,
                () -> goalService.createGoal(1L, goalDto));
    }

    @Test
    public void addSkillsToGoal() {
        GoalDto goalDto = GoalDto.builder().title("Title").description("description")
                .parentId(1L).skillIds(List.of(1L, 2L)).build();
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Goal createdGoal = Goal.builder().id(1L).title("Title").description("description")
                .parent(null).skillsToAchieve(List.of(skill1, skill2)).build();

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(2);
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(skill1, skill2));
        when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId()))
                .thenReturn(createdGoal);

        Goal goalForSkills = Goal.builder()
                .id(1L)
                .title("Goal Title")
                .skillsToAchieve(new ArrayList<>()) // Начально пустой набор навыков
                .build();
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goalForSkills));

        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(
                Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));

        GoalDto result = goalService.createGoal(1L, goalDto);
        assertNotNull(result);
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

