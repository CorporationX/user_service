package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTests {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    //    @Mock private SkillService skillService;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private GoalValidator goalValidator;
    @InjectMocks
    private GoalServiceImpl goalService;

    private Goal activeGoal, completedGoal;
    private GoalDto activeGoalDto, completedGoalDto;

    @BeforeEach
    void setUp() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(3);

        activeGoal = Goal.builder()
                .id(1L)
                .title("title1")
                .status(GoalStatus.ACTIVE)
                .deadline(deadline)
                .description("description1")
                .build();

        completedGoal = Goal.builder()
                .id(2L)
                .title("title2")
                .status(GoalStatus.COMPLETED)
                .deadline(deadline)
                .description("description2")
                .build();

        activeGoalDto = GoalDto.builder()
                .id(1L)
                .description("description1")
                .title("title1")
                .status(GoalStatus.ACTIVE)
                .deadline(deadline)
                .skillIds(Collections.emptyList())
                .userIds(new ArrayList<>())
                .build();

        completedGoalDto = GoalDto.builder()
                .id(2L)
                .description("description2")
                .title("title2")
                .status(GoalStatus.COMPLETED)
                .deadline(deadline)
                .skillIds(Collections.emptyList())
                .userIds(new ArrayList<>())
                .build();

        User testUser = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        Skill testSkill = Skill.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should throw NotFoundException when creating a goal with invalid user ID")
    void testCreateGoalInvalidUserId() {
        assertThrows(NotFoundException.class, () -> goalService.createGoal(0L, new GoalDto()),
                "User with id: 0 not found");
        assertThrows(NotFoundException.class, () -> goalService.createGoal(null, new GoalDto()),
                "User with id: null not found");
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent goal")
    void testUpdateGoalNonExistent() {
        assertThrows(NotFoundException.class, () -> goalService.updateGoal(0L, new GoalDto()),
                "Goal with id: 0 not found");
        assertThrows(NotFoundException.class, () -> goalService.updateGoal(null, new GoalDto()),
                "Goal with id: null not found");
    }

    @Test
    @DisplayName("Should successfully delete a goal")
    void testDeleteGoalValid() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(activeGoal));
        goalService.deleteGoal(1L);

        verify(goalRepository).delete(activeGoal);
    }

    @Test
    @DisplayName("Should fetch goals by user ID")
    void testGetGoalsByUser() {
        Stream<Goal> goalStream = Stream.of(activeGoal, completedGoal);
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goalStream);

        List<GoalDto> retrievedGoals = goalService.getGoalsByUser(1L, null);
        List<GoalDto> expectedGoals = List.of(activeGoalDto, completedGoalDto);

        assertIterableEquals(expectedGoals, retrievedGoals);
    }

    @Test
    @DisplayName("Should throw NotFoundException when creating a goal with null GoalDto")
    void testCreateGoalWithNullGoalDto() {
        Long userId = 1L;
        assertThrows(NotFoundException.class, () -> goalService.createGoal(userId, null),
                "GoalDto must not be null");
    }

    @Test
    @DisplayName("Should throw NotFoundException when the user for the goal is not found")
    void testCreateGoalUserNotFound() {
        Long userId = 1L;
        GoalDto newGoalDto = new GoalDto();
        newGoalDto.setTitle("New Goal");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> goalService.createGoal(userId, newGoalDto));
        assertEquals("User with id: 1 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null or invalid goal ID in update goal")
    void testUpdateGoalWithInvalidId() {
        assertThrows(NotFoundException.class, () -> goalService.updateGoal(null, new GoalDto()),
                "Goal ID must not be null");

        assertThrows(NotFoundException.class, () -> goalService.updateGoal(-1L, new GoalDto()),
                "Invalid goal ID: -1");
    }

    @Test
    @DisplayName("Should throw NotFoundException when goal to update is not found")
    void testUpdateGoalNotFound() {
        long goalId = 999L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> goalService.updateGoal(goalId, new GoalDto()));
        assertEquals("Goal with id: 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete an existing goal correctly")
    void testDeleteExistingGoal() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(activeGoal));

        goalService.deleteGoal(goalId);

        verify(goalRepository).delete(activeGoal);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent goal gracefully")
    void testDeleteNonExistentGoal() {
        long nonExistentGoalId = 999L;
        when(goalRepository.findById(nonExistentGoalId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> goalService.deleteGoal(nonExistentGoalId));
        assertEquals("Goal with id: 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should correctly find subtasks by goal ID")
    void testFindSubtasksByGoalId() {
        long parentId = 1L;
        when(goalRepository.findByParent(parentId)).thenReturn(Stream.of(activeGoal, completedGoal));

        List<GoalDto> subtasks = goalService.getSubtasksByGoalId(parentId, null);

        assertIterableEquals(List.of(activeGoalDto, completedGoalDto), subtasks, "Subtasks should match expected goals");
    }
}

