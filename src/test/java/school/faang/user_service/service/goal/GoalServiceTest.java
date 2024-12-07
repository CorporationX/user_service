package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalValidator goalValidator;
    @Spy
    private GoalMapper goalMapper = new GoalMapperImpl();
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;

    @Spy
    @InjectMocks
    private GoalService goalService;

    @Nested
    class TestCreate {

        long userId;
        User user;

        long parentId;
        Goal parentGoal;

        List<Long> skillToAchieveIds;
        GoalDto goalDto;

        Skill firstSkillFromDb;
        Skill secondSkillFromDb;
        List<Skill> skillsToAchieve;

        @BeforeEach
        public void setup() {
            userId = 1L;
            user = User.builder()
                    .id(userId)
                    .build();

            parentId = 1L;
            parentGoal = Goal.builder()
                    .id(parentId)
                    .build();

            skillToAchieveIds = List.of(1L, 2L);
            firstSkillFromDb = Skill.builder().id(1L).build();
            secondSkillFromDb = Skill.builder().id(2L).build();
            skillsToAchieve = List.of(firstSkillFromDb, secondSkillFromDb);

            goalDto = GoalDto.builder()
                    .parentId(parentId)
                    .skillToAchieveIds(skillToAchieveIds)
                    .build();
        }

        @Test
        public void testCreateGoal_Successfully() {
            Goal savedGoal = Goal.builder().id(2L).build();
            ArgumentCaptor<Goal> goalArgumentCaptor = ArgumentCaptor.forClass(Goal.class);
            when(userService.getUserById(userId)).thenReturn(user);
            doReturn(parentGoal).when(goalService).getGoalById(parentId);
            when(skillService.getSkillsByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieve);
            when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

            goalService.create(userId, goalDto);

            verify(goalValidator, times(1)).validateCreate(any(GoalDto.class), any(User.class));
            verify(goalRepository, times(1)).save(goalArgumentCaptor.capture());
            Goal goal = goalArgumentCaptor.getValue();
            assertEquals(1, goal.getUsers().size());
            assertEquals(userId, goal.getUsers().get(0).getId());
            assertEquals(parentGoal, goal.getParent());
            assertEquals(skillsToAchieve, goal.getSkillsToAchieve());
            assertEquals(GoalStatus.ACTIVE, goal.getStatus());
            verify(goalMapper, times(1)).toDto(savedGoal);
        }

        @Test
        public void testCreate_NotValidData() {
            when(userService.getUserById(userId)).thenReturn(user);
            doReturn(parentGoal).when(goalService).getGoalById(parentId);
            when(skillService.getSkillsByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieve);
            doThrow(IllegalArgumentException.class).when(goalValidator).validateCreate(goalDto, user);

            Assertions.assertThrows(IllegalArgumentException.class, () -> goalService.create(userId, goalDto));

            verify(goalValidator, times(1)).validateCreate(goalDto, user);
        }
    }

    @Nested
    class TestGetById {

        long id = 1L;

        @Test
        public void testGetGoalById_Successfully() {
            Goal goal = new Goal();
            when(goalRepository.findById(id)).thenReturn(Optional.of(goal));

            Goal goalById = goalService.getGoalById(id);

            assertEquals(goal, goalById);
        }

        @Test
        public void testGetGoalById_GoalNotExist() {
            when(goalRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> goalService.getGoalById(id));
        }
    }

    @Nested
    class TestUpdate {

        long goalId;
        Goal goal;

        long parentId;
        Goal parentGoal;

        List<Long> skillToAchieveIds;
        GoalDto goalDto;

        Skill firstSkill;
        Skill secondSkill;
        List<Skill> skillsToAchieve;

        @BeforeEach
        public void setup() {
            goalId = 2L;
            goal = Goal.builder()
                    .id(goalId)
                    .build();

            parentId = 1L;
            parentGoal = Goal.builder()
                    .id(parentId)
                    .build();

            skillToAchieveIds = List.of(1L, 2L);
            firstSkill = Skill.builder().id(1L).build();
            secondSkill = Skill.builder().id(2L).build();
            skillsToAchieve = List.of(firstSkill, secondSkill);

            goalDto = GoalDto.builder()
                    .parentId(parentId)
                    .skillToAchieveIds(skillToAchieveIds)
                    .build();
        }

        @Test
        public void testUpdateGoal_Successfully() {
            ArgumentCaptor<Goal> goalArgumentCaptor = ArgumentCaptor.forClass(Goal.class);
            doReturn(goal).when(goalService).getGoalById(goalId);
            doReturn(parentGoal).when(goalService).getGoalById(parentId);
            when(skillService.getSkillsByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieve);

            goalService.update(goalId, goalDto);

            verify(goalValidator, times(1)).validateUpdate(goal, goalDto);
            verify(goalRepository, times(1)).save(goalArgumentCaptor.capture());
            Goal goal = goalArgumentCaptor.getValue();
            assertEquals(parentGoal, goal.getParent());
            assertEquals(skillsToAchieve, goal.getSkillsToAchieve());
        }

        @Test
        public void testUpdate_GoalStatusChangeFromActiveToCompleted() {
            Skill thirdSkill = Skill.builder()
                    .id(2L)
                    .build();

            User firstUserFromDb = User.builder()
                    .id(1L)
                    .skills(new ArrayList<>(List.of(firstSkill, thirdSkill)))
                    .build();
            User secondUserFromDb = User.builder()
                    .id(2L)
                    .skills(new ArrayList<>())
                    .build();
            List<User> usersFromDb = List.of(firstUserFromDb, secondUserFromDb);

            List<Long> skillToAchieveIds = List.of(1L, 2L);
            GoalDto goalDto = GoalDto.builder()
                    .skillToAchieveIds(skillToAchieveIds)
                    .status(GoalStatus.COMPLETED.toString())
                    .build();

            goal.setStatus(GoalStatus.ACTIVE);
            goal.setUsers(usersFromDb);

            ArgumentCaptor<Goal> goalArgumentCaptor = ArgumentCaptor.forClass(Goal.class);
            doReturn(goal).when(goalService).getGoalById(goalId);
            when(skillService.getSkillsByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieve);

            goalService.update(goalId, goalDto);

            verify(goalRepository, times(1)).save(goalArgumentCaptor.capture());
            Goal goal = goalArgumentCaptor.getValue();
            assertIterableEquals(usersFromDb, goal.getUsers());
            assertThat(usersFromDb).hasSameElementsAs(goal.getUsers());
            goal.getUsers().forEach(user -> assertTrue(user.getSkills().containsAll(skillsToAchieve)));
            assertEquals(skillsToAchieve, goal.getSkillsToAchieve());
        }

        @Test
        public void testUpdate_NotValidData() {
            doReturn(goal).when(goalService).getGoalById(goalId);
            doReturn(parentGoal).when(goalService).getGoalById(parentId);
            when(skillService.getSkillsByIdIn(skillToAchieveIds)).thenReturn(skillsToAchieve);
            doThrow(IllegalArgumentException.class).when(goalValidator).validateUpdate(any(Goal.class), any(GoalDto.class));

            Assertions.assertThrows(IllegalArgumentException.class, () -> goalService.update(goalId, goalDto));

            verify(goalValidator, times(1)).validateUpdate(goal, goalDto);
        }
    }

    @Test
    public void testDelete_Successfully() {
        long goalId = 1L;

        goalRepository.deleteById(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }
}