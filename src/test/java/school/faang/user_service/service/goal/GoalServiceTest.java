package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.goal.GoalCompletedEventPublisher;
import school.faang.user_service.publisher.goal.GoalSetEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    private static final long USER_ID_ONE = 1L;
    private static final long USER_ID_TWO = 2L;
    private static final Long GOAL_ID = 1L;

    private static final String NEW_GOAL_TITLE = "New Goal";
    private static final String GOAL_DESCRIPTION = "Description";

    private static final int USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO = 0;
    private static final int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ZERO = 0;
    private static final int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE = 1;
    private static final int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_TWO = 2;

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalValidator goalValidator;

    @Mock
    private UserService userService;

    @Mock
    private GoalInvitationService goalInvitationService;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Mock
    private GoalSetEventPublisher goalSetEventPublisher;

    private GoalDto goalDto;
    private Goal goal;
    private User user1;
    private User user2;
    private Goal activeGoal;
    private List<GoalInvitation> goalInvitations;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(new ArrayList<>(List.of(user1)));

        goalDto = new GoalDto();
        goalDto.setTitle(NEW_GOAL_TITLE);
        goalDto.setDescription(GOAL_DESCRIPTION);
    }

    @Test
    @DisplayName("Throws exception when goal is not found")
    void whenGoalNotFoundThenThrowEntityNotFoundException() {
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                goalService.updateGoal(GOAL_ID, goalDto)
        );

        verify(goalRepository).findById(GOAL_ID);
    }

    @Nested
    @DisplayName("User Goal Limit Validation Tests")
    class UserGoalLimitTests {

        @Test
        @DisplayName("Throws exception when user exceeds goal limit during goal creation")
        void whenUserExceedsGoalLimitThenThrowExceptionOnCreate() {
            doThrow(DataValidationException.class).when(goalValidator).validateUserGoalLimit(USER_ID_ONE);

            assertThrows(DataValidationException.class, () ->
                    goalService.createGoal(USER_ID_ONE, goalDto)
            );

            verify(goalValidator).validateUserGoalLimit(USER_ID_ONE);
        }

        @Test
        @DisplayName("Does not throw exception when user does not exceed goal limit during goal creation")
        void whenUserDoesNotExceedGoalLimitThenDoNotThrowExceptionOnCreate() {
            when(userService.getUserById(USER_ID_ONE)).thenReturn(user1);
            when(goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), null)).thenReturn(goal);
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto result = goalService.createGoal(USER_ID_ONE, goalDto);

            assertNotNull(result);
            verify(goalValidator).validateUserGoalLimit(USER_ID_ONE);
            verify(goalMapper).toGoalDto(goal);
            verify(goalSetEventPublisher).publish(any());
        }
    }

    @Nested
    @DisplayName("Goal Status Validation Tests")
    class GoalStatusTests {

        @Test
        @DisplayName("Throws exception when goal is completed during update")
        void whenGoalIsCompletedThenThrowExceptionOnUpdate() {
            goal.setStatus(GoalStatus.COMPLETED);

            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            doThrow(DataValidationException.class).when(goalValidator).validateGoalStatusNotCompleted(goal);

            assertThrows(DataValidationException.class, () ->
                    goalService.updateGoal(GOAL_ID, goalDto)
            );

            verify(goalValidator).validateGoalStatusNotCompleted(goal);
        }

        @Test
        @DisplayName("Does not throw exception when goal is not completed during update")
        void whenGoalIsNotCompletedThenDoNotThrowExceptionOnUpdate() {
            goal.setStatus(GoalStatus.ACTIVE);
            goalDto.setStatus(GoalStatus.ACTIVE);

            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto result = goalService.updateGoal(GOAL_ID, goalDto);

            assertNotNull(result);
            verify(goalValidator).validateGoalStatusNotCompleted(goal);
            verify(goalMapper).toGoalDto(goal);
        }

        @Test
        @DisplayName("Should notify user when goalDto status is Completed")
        void whenGoalDtoIsCompletedThenDoNotThrowExceptionAndNotifyUsers() {
            goal.setStatus(GoalStatus.ACTIVE);
            goal.setId(GOAL_ID);
            goal.setTitle(NEW_GOAL_TITLE);
            goal.setUsers(List.of(User.builder().id(USER_ID_ONE).build()));
            goalDto.setStatus(GoalStatus.COMPLETED);

            when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(goal));
            when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

            GoalDto result = goalService.updateGoal(GOAL_ID, goalDto);

            assertNotNull(result);
            verify(goalValidator).validateGoalStatusNotCompleted(goal);
            verify(goalMapper).toGoalDto(goal);
            verify(goalCompletedEventPublisher).publish(any());
        }
    }

    @Nested
    class GoalTestWhileInactiveUser {

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
        @DisplayName("Delete users active goals and if somebody working with goals than " +
                "delete goal invitations for this user")
        void whenUserHasActiveGoalsAndSomebodyWorkWithThisGoalThenDeleteUserAndInvitationsFromGoal() {
            List<User> users = new ArrayList<>();
            users.add(user1);
            users.add(user2);

            activeGoal.setUsers(users);

            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_TWO, activeGoal.getUsers().size());

            goalService.deactivateActiveUserGoals(user1);

            verify(goalInvitationService)
                    .deleteGoalInvitationForUser(goalInvitations, user1);

            assertEquals(USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO, user1.getGoals().size());
            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE, activeGoal.getUsers().size());
        }

        @Test
        @DisplayName("Delete users active goals and if nobody working with goals than delete goal and invitations")
        void whenUserHasActiveGoalsAndNobodyWorkingWithThenDeleteGoalAndInvitationsToGoal() {
            List<User> users = new ArrayList<>();
            users.add(user1);

            activeGoal.setUsers(users);

            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE, activeGoal.getUsers().size());

            goalService.deactivateActiveUserGoals(user1);

            verify(goalRepository)
                    .deleteById(any());
            verify(goalInvitationService)
                    .deleteGoalInvitations(goalInvitations);

            assertEquals(USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO, user1.getGoals().size());
            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ZERO, activeGoal.getUsers().size());
        }
    }
}