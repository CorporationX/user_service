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
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalServiceValidator;
import school.faang.user_service.validator.SkillServiceValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    private static final long GOAL_ID = 1L;
    private static final long USER_ID_ONE = 1L;
    private final static long USER_ID_TWO = 2L;
    private static final long SKILL_ID = 1L;
    private static final String NEW_GOAL_TITLE = "New Goal";
    private static final String EXISTING_GOAL_TITLE = "Existing Goal";
    private static final String GOAL_DESCRIPTION = "Description";
    private final static int USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO = 0;
    private final static int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ZERO = 0;
    private final static int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE = 1;
    private final static int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_TWO = 2;

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalServiceValidator goalServiceValidator;

    @Mock
    private SkillServiceValidator skillServiceValidator;

    @Mock
    private GoalInvitationService goalInvitationService;

    private GoalDto goalDto;
    private Goal goal;
    private User user1;
    private User user2;
    private Goal activeGoal;
    private List<GoalInvitation> goalInvitations;

    @BeforeEach
    public void setUp() {
        goalDto = GoalDto.builder()
                .id(GOAL_ID)
                .title(NEW_GOAL_TITLE)
                .description(GOAL_DESCRIPTION)
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(SKILL_ID))
                .build();

        goal = new Goal();
        goal.setId(GOAL_ID);
        goal.setTitle(EXISTING_GOAL_TITLE);
        goal.setDescription(GOAL_DESCRIPTION);
        goal.setStatus(GoalStatus.ACTIVE);
    }

    @Nested
    @DisplayName("Goal Creation Tests")
    class CreateGoalTests {

        @Test
        @DisplayName("Creates goal successfully when valid input is provided")
        @Transactional
        void whenValidInputThenCreateGoalSuccessfully() {
            when(goalRepository.countActiveGoalsPerUser(USER_ID_ONE)).thenReturn(0);
            when(goalMapper.toGoal(goalDto)).thenReturn(goal);

            GoalDto createdGoal = goalService.createGoal(USER_ID_ONE, goalDto);

            verify(goalRepository).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
            verify(skillService).create(goal.getSkillsToAchieve(), USER_ID_ONE);
            verify(goalServiceValidator).validateUserGoalLimit(0);

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
            when(goalMapper.toGoal(goalDto)).thenReturn(goal);
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto updatedGoal = goalService.updateGoal(GOAL_ID, goalDto);

            verify(goalRepository).save(goal);
            verify(skillService).addSkillToUsers(goalRepository.findUsersByGoalId(GOAL_ID), GOAL_ID);
            verify(goalServiceValidator).validateGoalStatusNotCompleted(goal);
            verify(skillServiceValidator).validateExistByTitle(goal.getSkillsToAchieve());

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
            Stream<Goal> goalsStream = Stream.of(goal);
            when(goalRepository.findByParent(GOAL_ID)).thenReturn(goalsStream);

            goalService.deleteGoal(GOAL_ID);

            verify(goalRepository).deleteById(GOAL_ID);
            verify(goalServiceValidator).validateGoalsExist(goalsStream);
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

            when(goalRepository.findGoalsByUserId(USER_ID_ONE)).thenReturn(goalsStream);
            when(goalFilters.stream()).thenReturn(Stream.of());

            goalService.getGoalsByUser(USER_ID_ONE, null, null, null, null);

            verify(goalRepository).findGoalsByUserId(USER_ID_ONE);
            verify(goalFilters).stream();
        }
    }

    @Nested
    class GoalTestWhileInactiveUser{

        @BeforeEach
        void init() {
            user1 = User.builder()
                    .id(USER_ID_ONE)
                    .build();
            user2 = User.builder()
                    .id(USER_ID_TWO)
                    .build();

            goalInvitations = List.of(
                    GoalInvitation.builder()
                            .invited(user1)
                            .inviter(user1)
                            .build(),
                    GoalInvitation.builder()
                            .invited(user2)
                            .inviter(user2)
                            .build());

            activeGoal = Goal.builder()
                    .status(GoalStatus.ACTIVE)
                    .invitations(goalInvitations)
                    .build();

            Goal completedGoal = Goal.builder()
                    .status(GoalStatus.COMPLETED)
                    .build();

            List<Goal> goals = new ArrayList<>();
            goals.add(activeGoal);
            goals.add(completedGoal);

            user1.setGoals(goals);
        }

        @Test
        @DisplayName("Удаляем у юзера активные цели и если у цели остались пользователи, " +
                "кто работает над этой целью, то удаляем приглашения присоединения к цели для этого юзера")
        void whenUserHasActiveGoalsAndSomebodyWorkWithThisGoalThenDeleteUserAndInvitationsFromGoal() {
            List<User> users = new ArrayList<>();
            users.add(user1);
            users.add(user2);

            activeGoal.setUsers(users);

            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_TWO, activeGoal.getUsers().size());

            goalService.deactivateActiveUserGoalsAndDeleteIfNoOneIsWorkingWith(user1);

            verify(goalInvitationService)
                    .deleteGoalInvitationForUser(goalInvitations, user1);

            assertEquals(USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO, user1.getGoals().size());
            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE, activeGoal.getUsers().size());
        }

        @Test
        @DisplayName("Удаляем у юзера активные цели и если у цели не остались пользователи работающие над ней," +
                " то удаляем эту цель и все приглашения к этой цели")
        void whenUserHasActiveGoalsAndNobodyWorkingWithThenDeleteGoalAndInvitationsToGoal() {
            List<User> users = new ArrayList<>();
            users.add(user1);

            activeGoal.setUsers(users);

            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE, activeGoal.getUsers().size());

            goalService.deactivateActiveUserGoalsAndDeleteIfNoOneIsWorkingWith(user1);

            verify(goalRepository)
                    .deleteById(any());
            verify(goalInvitationService)
                    .deleteGoalInvitations(goalInvitations);

            assertEquals(USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO, user1.getGoals().size());
            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ZERO, activeGoal.getUsers().size());
        }
   }
}