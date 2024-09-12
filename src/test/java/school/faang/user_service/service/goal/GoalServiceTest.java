package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalInvitationService goalInvitationService;

    @Nested
    class PositiveTests {

        private User user1;
        private User user2;
        private Goal activeGoal;
        private List<GoalInvitation> goalInvitations;

        private final static int USER_ACTIVE_GOALS_FINAL_SIZE_IS_ZERO = 0;
        private final static int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ZERO = 0;
        private final static int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_ONE = 1;
        private final static int ACTIVE_GOAL_USERS_FINAL_SIZE_IS_TWO = 2;

        private final static long USER_ID_ONE = 1L;
        private final static long USER_ID_TWO = 2L;

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

            assertEquals(ACTIVE_GOAL_USERS_FINAL_SIZE_IS_TWO, activeGoal.getUsers().size());

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