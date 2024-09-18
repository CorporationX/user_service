package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.service.goal.filter.GoalFilterByAnySkills;
import school.faang.user_service.service.goal.filter.GoalFilterByStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<GoalFilter> goalFilters = new ArrayList<>();

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        skill = new Skill();
        skill.setId(10L);

        goal = new Goal();
        goal.setId(100L);
        goal.setTitle("Learning");
        goal.setSkillsToAchieve(new ArrayList<>(List.of(skill)));
    }

    @Test
    @DisplayName("Success create new goal")
    public void testCreateNewGoalIsSuccess() {
        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(1);
        when(skillRepository.countExisting(List.of(skill.getId()))).thenReturn(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        goalService.createGoal(user.getId(), goal);

        verify(goalRepository).save(goal);
    }

    @Test
    @DisplayName("Success update active goal")
    public void testUpdateActiveGoalDtoIsSuccess() {
        Goal existingGoal = new Goal();
        existingGoal.setId(100L);
        existingGoal.setSkillsToAchieve(new ArrayList<>());
        existingGoal.setUsers(new ArrayList<>());
        existingGoal.setStatus(GoalStatus.ACTIVE);

        when(skillRepository.countExisting(List.of(skill.getId()))).thenReturn(1);
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(existingGoal));
        goal.setStatus(GoalStatus.ACTIVE);

        goalService.updateGoal(goal);

        verify(goalRepository).save(existingGoal);
        assertEquals(goal.getSkillsToAchieve(), existingGoal.getSkillsToAchieve());
    }

    @Test
    @DisplayName("Success update completed goal")
    public void testUpdateCompletedGoalDtoIsSuccess() {
        Goal existingGoal = new Goal();
        existingGoal.setId(100L);
        skill.setUsers(new ArrayList<>());
        existingGoal.setSkillsToAchieve(new ArrayList<>(List.of(skill)));
        existingGoal.setUsers(new ArrayList<>(List.of(user)));
        existingGoal.setStatus(GoalStatus.ACTIVE);

        when(skillRepository.countExisting(List.of(skill.getId()))).thenReturn(1);
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(existingGoal));
        goal.setStatus(GoalStatus.COMPLETED);

        goalService.updateGoal(goal);

        assertEquals(GoalStatus.COMPLETED, existingGoal.getStatus());
        assertTrue(skill.getUsers().contains(user));
        verify(goalRepository).save(existingGoal);
    }

    @Test
    @DisplayName("Success find subtasks")
    public void testFindSubtaskByGoalId() {
        Goal subtaskOne = Goal.builder()
                .id(2L)
                .title("Learning Java")
                .parent(goal)
                .build();

        Goal subtaskTwo = Goal.builder()
                .id(2L)
                .title("Learning SQL")
                .parent(goal)
                .build();

        when(goalRepository.existsById(goal.getId())).thenReturn(true);
        when(goalRepository.findByParent(goal.getId())).thenReturn(Stream.of(subtaskOne, subtaskTwo));

        goalService.findSubtaskByGoalId(goal.getId());

        verify(goalRepository).findByParent(goal.getId());
    }

    @Test
    @DisplayName("Success find goal by filter")
    public void testFindUserGoalByFilter() {

        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setStatus(GoalStatus.COMPLETED);
        filterDto.setSkillIds(new ArrayList<>(List.of(3L)));


        Goal goalOne = Goal.builder()
                .id(1L)
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(
                        new ArrayList<>(List.of(
                                Skill.builder().id(3L).build(),
                                Skill.builder().id(4L).build()))
                )
                .build();

        Goal goalTwo = Goal.builder()
                .id(2L)
                .status(GoalStatus.COMPLETED)
                .skillsToAchieve(
                        new ArrayList<>(List.of(
                                Skill.builder().id(1L).build(),
                                Skill.builder().id(2L).build()))
                )
                .build();

        Goal goalThree = Goal.builder()
                .id(3L)
                .status(GoalStatus.COMPLETED)
                .skillsToAchieve(
                        new ArrayList<>(List.of(
                                Skill.builder().id(3L).build(),
                                Skill.builder().id(1L).build()))
                )
                .build();

        User user = User.builder()
                .id(100L)
                .goals(new ArrayList<>(List.of(
                        goalOne,
                        goalTwo,
                        goalThree
                )))
                .build();


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(goalFilters.stream()).thenReturn(Stream.of(
                new GoalFilterByStatus(),
                new GoalFilterByAnySkills()
        ));

        int expectedGoalsSize = 1;
        Long expectedGoalId = 3L;
        GoalStatus expectedGoalStatus = GoalStatus.COMPLETED;

        List<Goal> resultGoals = goalService.findGoalsByUser(user.getId(), filterDto);
        Long resultGoalId = resultGoals.get(0).getId();
        GoalStatus resultGoalStatus = resultGoals.get(0).getStatus();

        assertEquals(expectedGoalsSize, resultGoals.size());
        assertEquals(expectedGoalId, resultGoalId);
        assertEquals(expectedGoalStatus, resultGoalStatus);
    }

    @Test
    @DisplayName("Incorrect goal title")
    public void testGoalTitleIsInvalid() {
        goal.setTitle(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(user.getId(), goal)
        );
    }

    @Test
    @DisplayName("Incorrect amount active goals")
    public void testUserActiveGoalCountIsInvalid() {
        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(3);

        assertThrows(
                IllegalStateException.class,
                () -> goalService.createGoal(user.getId(), goal)
        );
    }

    @Test
    @DisplayName("Incorrect goal skill")
    public void testGoalSkillsIsInvalid() {

        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(1);
        when(skillRepository.countExisting(List.of(skill.getId()))).thenReturn(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(user.getId(), goal)
        );
    }

    @Test
    @DisplayName("Incorrect goalEntity status")
    public void updateGoalIsInvalid() {
        goal.setStatus(GoalStatus.COMPLETED);
        when(skillRepository.countExisting(List.of(skill.getId()))).thenReturn(1);
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        assertThrows(
                IllegalArgumentException.class,
                () -> goalService.updateGoal(goal)
        );
    }

    @Test
    @DisplayName("Incorrect goalId")
    public void testValidateGoalIdIsInvalid() {
        when(goalRepository.existsById(goal.getId())).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> goalService.deleteGoal(goal.getId())
        );
    }
}
