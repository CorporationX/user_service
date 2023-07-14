package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EmptyGoalsException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class GoalServiceTests {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    public void setup() {
        Mockito.when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(createGoal(1L, "Goal 1", GoalStatus.ACTIVE)));
        Mockito.when(goalRepository.findGoalsByUserId(2L)).thenReturn(Stream.empty());
    }

    @Test
    public void testGetGoalsByUser_Success() {
        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        List<GoalDto> actualGoals = goalService.getGoalsByUser(userId, filter);

        assertEquals(1, actualGoals.size());
        assertEquals(1L, actualGoals.get(0).getId());
        assertEquals("Goal 1", actualGoals.get(0).getTitle());
        assertEquals(GoalStatus.ACTIVE, actualGoals.get(0).getStatus());
    }

    private Goal createGoal(Long id, String title, GoalStatus status) {
        Goal goal = new Goal();
        goal.setId(id);
        goal.setTitle(title);
        goal.setStatus(status);
        goal.setParent(new Goal());
        goal.setSkillsToAchieve(new ArrayList<>());
        return goal;
    }

    @Test
    public void testGetGoalsByUser_EmptyGoals() {
        Long userId = 2L;
        GoalFilterDto filter = new GoalFilterDto();

        EmptyGoalsException exception = assertThrows(EmptyGoalsException.class, () -> goalService.getGoalsByUser(userId, filter));

        String expectedErrorMessage = "Список целей пользователя пуст.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testGetGoalsByUser_InvalidUserId() {
        Long userId = null;
        GoalFilterDto filter = new GoalFilterDto();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.getGoalsByUser(userId, filter));

        String expectedErrorMessage = "Необходимо указать идентификатор пользователя.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testGetGoalsByUser_InvalidFilter() {
        Long userId = 1L;
        GoalFilterDto filter = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.getGoalsByUser(userId, filter));

        String expectedErrorMessage = "Необходимо указать фильтр целей.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testGetGoalsByUser_FilterByStatus() {
        long userId = 3L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        Goal goal1 = createGoal(1L, "Goal 1", GoalStatus.ACTIVE);
        Goal goal2 = createGoal(2L, "Goal 2", GoalStatus.COMPLETED);
        Goal goal3 = createGoal(3L, "Goal 3", GoalStatus.ACTIVE);
        Goal goal4 = createGoal(4L, "Goal 4", GoalStatus.IN_PROGRESS);

        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3, goal4));

        List<GoalDto> actualGoals = assertDoesNotThrow(() -> goalService.getGoalsByUser(userId, filter));

        assertEquals(2, actualGoals.size());
        assertEquals(GoalStatus.ACTIVE, actualGoals.get(0).getStatus());
        assertEquals(GoalStatus.ACTIVE, actualGoals.get(1).getStatus());
    }

    @Test
    public void testGetGoalsByUser_FilterByTitle() {
        long userId = 3L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Goal 1");

        Goal goal1 = createGoal(1L, "Goal 1", GoalStatus.ACTIVE);
        Goal goal2 = createGoal(2L, "Goal 2", GoalStatus.COMPLETED);
        Goal goal3 = createGoal(3L, "Goal 1", GoalStatus.ACTIVE);
        Goal goal4 = createGoal(4L, "Goal 3", GoalStatus.IN_PROGRESS);

        Mockito.when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3, goal4));

        List<GoalDto> actualGoals = assertDoesNotThrow(() -> goalService.getGoalsByUser(userId, filter));

        assertEquals(2, actualGoals.size());
        assertEquals("Goal 1", actualGoals.get(0).getTitle());
        assertEquals("Goal 1", actualGoals.get(1).getTitle());
    }

    @Test
    public void testGetGoalsByUser_FilterByParentGoal() {
        Goal parentGoal1 = createGoal(1L, "Parent Goal 1", GoalStatus.ACTIVE);
        Goal parentGoal2 = createGoal(2L, "Parent Goal 2", GoalStatus.ACTIVE);

        Goal childGoal1 = createGoal(3L, "Child Goal 1", GoalStatus.ACTIVE);
        childGoal1.setParent(parentGoal1);

        Goal childGoal2 = createGoal(4L, "Child Goal 2", GoalStatus.ACTIVE);
        childGoal2.setParent(parentGoal1);

        Goal childGoal3 = createGoal(5L, "Child Goal 3", GoalStatus.ACTIVE);
        childGoal3.setParent(parentGoal2);

        Mockito.when(goalRepository.findGoalsByUserId(Mockito.anyLong())).thenReturn(Stream.of(childGoal1, childGoal2, childGoal3));

        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setParentId(1L);

        List<GoalDto> actualGoals = assertDoesNotThrow(() -> goalService.getGoalsByUser(userId, filter));

        Assertions.assertEquals(2, actualGoals.size());
        Assertions.assertEquals(childGoal1.getId(), actualGoals.get(0).getId());
        Assertions.assertEquals(childGoal2.getId(), actualGoals.get(1).getId());
    }

    @Test
    public void testGetGoalsByUser_MultipleFilters() {
        Goal goal1 = createGoal(1L, "Goal 1", GoalStatus.ACTIVE);
        goal1.setSkillsToAchieve(Arrays.asList(createSkill(1L, "Skill 1"), createSkill(2L, "Skill 2")));

        Goal goal2 = createGoal(2L, "Goal 2", GoalStatus.COMPLETED);
        goal2.setSkillsToAchieve(Arrays.asList(createSkill(1L, "Skill 1"), createSkill(3L, "Skill 3")));

        Goal goal3 = createGoal(3L, "Goal 3", GoalStatus.ACTIVE);
        goal3.setSkillsToAchieve(Arrays.asList(createSkill(2L, "Skill 2"), createSkill(4L, "Skill 4")));

        Mockito.when(goalRepository.findGoalsByUserId(Mockito.anyLong())).thenReturn(Stream.of(goal1, goal2, goal3));

        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);
        filter.setSkillIds(Arrays.asList(1L, 2L));

        List<GoalDto> actualGoals = assertDoesNotThrow(() -> goalService.getGoalsByUser(userId, filter));

        Assertions.assertEquals(1, actualGoals.size());
        Assertions.assertEquals(goal1.getId(), actualGoals.get(0).getId());
    }

    private Skill createSkill(long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);
        skill.setUsers(new ArrayList<>());
        skill.setGuarantees(new ArrayList<>());
        skill.setEvents(new ArrayList<>());
        skill.setGoals(new ArrayList<>());
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());
        return skill;
    }

    @Test
    public void testConvertToDto_Success() {
        Goal goal = createGoal(1L, "Goal 1", GoalStatus.ACTIVE);

        GoalDto dto = assertDoesNotThrow(() -> goalService.convertToDto(goal));

        assertEquals(1L, dto.getId());
        assertEquals("Goal 1", dto.getTitle());
        assertEquals(GoalStatus.ACTIVE, dto.getStatus());
    }

    @Test
    public void testConvertToDto_NullGoal() {
        Goal goal = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.convertToDto(goal));

        String expectedErrorMessage = "Необходимо указать цель.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testConvertToDto_NullGoalId() {
        Goal goal = createGoal(null, "Goal 1", GoalStatus.ACTIVE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.convertToDto(goal));

        String expectedErrorMessage = "Идентификатор цели не может быть пустым.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testConvertToDto_NullGoalTitle() {
        Goal goal = createGoal(1L, null, GoalStatus.ACTIVE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.convertToDto(goal));

        String expectedErrorMessage = "Название цели не может быть пустым.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testConvertToDto_EmptyGoalTitle() {
        Goal goal = createGoal(1L, "", GoalStatus.ACTIVE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.convertToDto(goal));

        String expectedErrorMessage = "Название цели не может быть пустым.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testConvertToDto_NullGoalStatus() {
        Goal goal = createGoal(1L, "Goal 1", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.convertToDto(goal));

        String expectedErrorMessage = "Статус цели не может быть пустым.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

}