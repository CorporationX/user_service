package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDTO;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.adapter.GoalRepositoryAdapter;
import school.faang.user_service.repository.adapter.SkillRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

  @InjectMocks private GoalService goalService;

  @Mock private GoalRepository goalRepository;
  @Mock private GoalMapper goalMapper;
  @Mock private SkillRepositoryAdapter skillRepositoryAdapter;
  @Mock private GoalRepositoryAdapter goalRepositoryAdapter;
  @Mock private UserRepositoryAdapter userRepositoryAdapter;
  private GoalDTO goalDTO;
  private List<GoalFilter> filters;

  @Captor private ArgumentCaptor<Goal> captor;

  @BeforeEach
  void setUp() {
    goalDTO = new GoalDTO();
    goalDTO.setStatus("ACTIVE");
    goalDTO.setTitle("Title");
    goalDTO.setDescription("desc");
    goalDTO.setSkillToAchieveIds(List.of(1L, 2L));

    GoalFilter goalFilterMock = Mockito.mock(GoalFilter.class);
    filters = List.of(goalFilterMock);
    goalService =
        new GoalService(
            goalRepository,
            goalMapper,
            skillRepositoryAdapter,
            userRepositoryAdapter,
            filters,
            goalRepositoryAdapter);
  }

  @Test
  public void testGetSubGoalsWithFilters() {

    GoalFilterDTO goalFilterDTO = new GoalFilterDTO();
    goalFilterDTO.setTitle("Other Title");
    goalFilterDTO.setStatus(GoalStatus.COMPLETED.name());

    Goal parent = new Goal();
    parent.setId(1L);
    parent.setTitle("Title");
    parent.setStatus(GoalStatus.ACTIVE);
    parent.setUsers(new ArrayList<>());
    parent.setSkillsToAchieve(new ArrayList<>());

    Goal goal2 = new Goal();
    goal2.setId(2L);
    goal2.setTitle("Other Title");
    goal2.setStatus(GoalStatus.COMPLETED);
    goal2.setUsers(new ArrayList<>());
    goal2.setSkillsToAchieve(new ArrayList<>());
    goal2.setParent(parent);

    GoalDTO dto = new GoalDTO();
    dto.setTitle("Other Title");

    List<Goal> goals = List.of(goal2);

    Mockito.when(goalRepository.findByParent(parent.getId())).thenReturn(goals.stream());
    Mockito.when(filters.get(0).isApplicable(goalFilterDTO)).thenReturn(true);
    Mockito.when(filters.get(0).apply(goals, goalFilterDTO)).thenReturn(List.of(goal2));
    Mockito.when(goalMapper.toDtoList(List.of(goal2))).thenReturn(List.of(dto));
    List<GoalDTO> result = goalService.getSubGoals(parent.getId(), goalFilterDTO);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("Other Title", result.get(0).getTitle());
  }

  @Test
  public void testGetGoalsByUserWithFilters() {
    Long userId = 1L;

    GoalFilterDTO goalFilterDTO = new GoalFilterDTO();
    goalFilterDTO.setTitle("Title");

    Goal goal1 = new Goal();
    goal1.setId(1L);
    goal1.setTitle("Title");
    goal1.setStatus(GoalStatus.ACTIVE);
    goal1.setUsers(new ArrayList<>());
    goal1.setSkillsToAchieve(new ArrayList<>());

    Goal goal2 = new Goal();
    goal2.setId(2L);
    goal2.setTitle("Other Title");
    goal2.setStatus(GoalStatus.COMPLETED);
    goal2.setUsers(new ArrayList<>());
    goal2.setSkillsToAchieve(new ArrayList<>());

    GoalDTO dto = new GoalDTO();
    dto.setTitle("Title");

    List<Goal> goals = Arrays.asList(goal1, goal2);

    Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals.stream());
    Mockito.when(filters.get(0).isApplicable(goalFilterDTO)).thenReturn(true);
    Mockito.when(filters.get(0).apply(goals, goalFilterDTO)).thenReturn(List.of(goal1));
    Mockito.when(goalMapper.toDtoList(List.of(goal1))).thenReturn(List.of(dto));
    List<GoalDTO> result = goalService.getGoalsByUser(userId, goalFilterDTO);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("Title", result.get(0).getTitle());
  }

  @Test
  public void testDeleteSuccess() {
    Long goalId = 1L;

    Goal parentGoal = new Goal();
    parentGoal.setId(2L);
    Goal goal = new Goal();
    goal.setId(goalId);
    goal.setParent(parentGoal);
    Mockito.when(goalRepositoryAdapter.getById(goalId)).thenReturn(goal);
    Mockito.when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(new Goal()));

    goalService.deleteGoal(goalId);

    Assertions.assertNull(parentGoal.getParent());

    Mockito.verify(goalRepository, Mockito.times(1)).delete(captor.capture());

    Goal deletedGoal = captor.getValue();
    Assertions.assertEquals(goalId, deletedGoal.getId());
  }

  @Test
  public void testUpdateGoalSuccess() {
    Long goalId = 1L;
    GoalDTO goalDTO = new GoalDTO();
    goalDTO.setTitle("Updated Goal");
    goalDTO.setDescription("Updated Description");
    goalDTO.setDeadline(LocalDateTime.now().plusDays(10));
    goalDTO.setSkillToAchieveIds(List.of(1L, 2L));
    goalDTO.setStatus(GoalStatus.ACTIVE.name());

    Goal existingGoal = new Goal();
    existingGoal.setId(goalId);
    existingGoal.setUsers(new ArrayList<>());
    existingGoal.setTitle("Old Title");
    existingGoal.setDescription("Old Description");
    existingGoal.setDeadline(LocalDateTime.now().plusDays(5));
    existingGoal.setStatus(GoalStatus.ACTIVE);

    Skill skill1 = new Skill();
    skill1.setId(1L);
    skill1.setGoals(new ArrayList<>());
    Skill skill2 = new Skill();
    skill2.setId(2L);
    skill2.setGoals(new ArrayList<>());
    existingGoal.setSkillsToAchieve(new ArrayList<>(List.of(skill1)));

    Mockito.when(skillRepositoryAdapter.skillsExist(goalDTO.getSkillToAchieveIds()))
        .thenReturn(true);
    Mockito.when(goalRepositoryAdapter.getById(goalId)).thenReturn(existingGoal);
    Mockito.when(skillRepositoryAdapter.findAllById(goalDTO.getSkillToAchieveIds()))
        .thenReturn(List.of(skill1, skill2));
    Mockito.when(goalRepository.save(Mockito.any(Goal.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    goalService.updateGoal(goalId, goalDTO);

    ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);
    Mockito.verify(goalRepository, Mockito.times(1)).save(captor.capture());
    Goal savedGoal = captor.getValue();

    Assertions.assertEquals("Updated Goal", savedGoal.getTitle());
    Assertions.assertEquals("Updated Description", savedGoal.getDescription());
    Assertions.assertEquals(2, savedGoal.getSkillsToAchieve().size());
    Assertions.assertEquals(GoalStatus.ACTIVE, savedGoal.getStatus());
  }

  @Test
  public void testUpdateWithGoalCompleted() {
    Goal goal = new Goal();
    goal.setStatus(GoalStatus.COMPLETED);
    Mockito.when(skillRepositoryAdapter.skillsExist(goalDTO.getSkillToAchieveIds()))
        .thenReturn(true);
    Mockito.when(goalRepositoryAdapter.getById(1L)).thenReturn(goal);

        Assertions.assertThrows(DataValidationException.class, () -> goalService.updateGoal(1L, goalDTO));
    }


    @Test
    public void testCreateWithSkillsNotExists() {
        Mockito.when(skillRepositoryAdapter.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(1L, goalDTO));
    }

    @Test
    public void testCreateUserHaveMoreActiveGoalsThanIsAllowed() {
        long userId = 1L;
        Mockito.when(skillRepositoryAdapter.skillsExist(goalDTO.getSkillToAchieveIds())).thenReturn(true);
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(4);
        Assertions.assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDTO));
    }

  @Test
  public void testCreate() {
    long userId = 1L;
    Goal goalFromDTO = new Goal();
    goalFromDTO.setTitle("Title");
    User user;
    Mockito.when(skillRepositoryAdapter.skillsExist(goalDTO.getSkillToAchieveIds()))
        .thenReturn(true);
    Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
    Mockito.when(userRepositoryAdapter.getById(userId)).thenReturn(user = new User());
    user.setGoals(new ArrayList<>());
    Mockito.when(skillRepositoryAdapter.findAllById(goalDTO.getSkillToAchieveIds()))
        .thenReturn(new ArrayList<>());
    Mockito.when(goalMapper.toEntity(goalDTO)).thenReturn(goalFromDTO);
    goalService.createGoal(userId, goalDTO);

    Mockito.verify(goalRepository, Mockito.times(1)).save(captor.capture());
    Goal goal = captor.getValue();
    Assertions.assertEquals(goalDTO.getTitle(), goal.getTitle());
  }
}
