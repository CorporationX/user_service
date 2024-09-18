package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalServiceValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    private static final long GOAL_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long SKILL_ID = 1L;
    private static final String NEW_GOAL_TITLE = "New Goal";
    private static final String EXISTING_GOAL_TITLE = "Existing Goal";
    private static final String GOAL_DESCRIPTION = "Description";

    @InjectMocks
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalServiceValidator goalServiceValidator;

    private GoalDto goalDto;
    private Goal goal;
    private User user;

    @BeforeEach
    public void setUp() {
        goalDto = GoalDto.builder()
                .id(GOAL_ID)
                .title(NEW_GOAL_TITLE)
                .description(GOAL_DESCRIPTION)
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(SKILL_ID))
                .build();

        user = new User();
        user.setId(USER_ID);

        goal = new Goal();
        goal.setId(GOAL_ID);
        goal.setTitle(EXISTING_GOAL_TITLE);
        goal.setDescription(GOAL_DESCRIPTION);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(List.of(user));
    }

    @Nested
    @DisplayName("Goal Creation Tests")
    class CreateGoalTests {

        @Test
        @DisplayName("Creates goal successfully when valid input is provided")
        @Transactional
        void whenValidInputThenCreateGoalSuccessfully() {
            when(userService.getUserById(USER_ID)).thenReturn(user);
            when(goalRepository.countActiveGoalsPerUser(USER_ID)).thenReturn(0);
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);
            when(goalRepository.create(NEW_GOAL_TITLE, GOAL_DESCRIPTION, null)).thenReturn(goal);

            GoalDto createdGoal = goalService.createGoal(USER_ID, goalDto);

            verify(goalRepository).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
            verify(goalServiceValidator).validateUserGoalLimit(0);
            verify(skillService).addSkillsToUserId(any(), eq(USER_ID));
            verify(skillService).addSkillsToGoal(any(), eq(goal));
            assertEquals(goalDto, createdGoal);
        }
    }

    @Nested
    @DisplayName("Goal Update Tests")
    class UpdateGoalTests {

        @Test
        @DisplayName("Updates goal successfully when the goal exists")
        @Transactional
        void whenGoalExistsThenUpdateSuccessfully() {
            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto updatedGoal = goalService.updateGoal(GOAL_ID, goalDto);

            verify(goalRepository).save(goal);
            verify(goalServiceValidator).validateGoalStatusNotCompleted(goal);
            assertNotNull(updatedGoal);
            assertEquals(goalDto.getTitle(), updatedGoal.getTitle());
        }

        @Test
        @DisplayName("Throws NotFoundException when the goal does not exist")
        @Transactional
        void whenGoalDoesNotExistThenThrowNotFoundException() {
            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> goalService.updateGoal(GOAL_ID, goalDto));
            verify(goalRepository, never()).save(any(Goal.class));
        }
    }

    @Nested
    @DisplayName("Goal Deletion Tests")
    class DeleteGoalTests {

        @Test
        @DisplayName("Deletes goal successfully when the goal exists")
        @Transactional
        void whenGoalExistsThenDeleteSuccessfully() {
            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));

            goalService.deleteGoal(GOAL_ID);

            verify(goalRepository).deleteById(GOAL_ID);
        }
    }

    @Nested
    @DisplayName("Subtasks Fetching Tests")
    class FetchSubtasksTests {

        @Test
        @DisplayName("Fetches subtasks successfully when goal ID is provided")
        @Transactional
        void whenGoalIdProvidedThenFetchSubtasksSuccessfully() {
            Stream<Goal> goalsStream = Stream.of(goal);

            when(goalRepository.findByParent(GOAL_ID)).thenReturn(goalsStream);
            when(goalFilters.stream()).thenReturn(Stream.of());

            goalService.findSubtasksByGoalId(GOAL_ID, null, null, null, null);

            verify(goalRepository).findByParent(GOAL_ID);
            verify(goalFilters).stream();
        }
    }

    @Nested
    @DisplayName("Goals Fetching Tests")
    class FetchGoalsTests {

        @Test
        @DisplayName("Fetches goals successfully when user ID is provided")
        @Transactional
        void whenUserIdProvidedThenFetchGoalsSuccessfully() {
            Stream<Goal> goalsStream = Stream.of(goal);

            when(goalRepository.findGoalsByUserId(USER_ID)).thenReturn(goalsStream);
            when(goalFilters.stream()).thenReturn(Stream.of());

            goalService.getGoalsByUser(USER_ID, null, null, null, null);

            verify(goalRepository).findGoalsByUserId(USER_ID);
            verify(goalFilters).stream();
        }
    }
}

