package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationInvitationTest {

    private final ValidationInvitation validationInvitation = new ValidationInvitation();

    Goal goal = Goal.builder()
            .id(2L)
            .build();

    User invited = User.builder()
            .id(3L)
            .aboutMe("AboutMe")
            .city("Moscow")
            .username("MiguelHernandez")
            .goals(new ArrayList<>())
            .build();

    GoalInvitation goalInvitationEntity = GoalInvitation.builder()
            .id(10L)
            .goal(goal)
            .invited(invited)
            .status(RequestStatus.ACCEPTED)
            .build();


    @Test
    void testAcceptGoalInvitation_invitedUserHasMaxGoals_throwsIllegalArgumentException() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal());
        goals.add(new Goal());
        goals.add(new Goal());

        User invited = User.builder()
                .id(3L)
                .aboutMe("AboutMe")
                .city("Moscow")
                .username("RicardoIdris")
                .goals(goals)
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () ->
                validationInvitation.acceptGoalInvitation(goalInvitationEntity, invited, 3L));
        assertEquals(e.getMessage(), "Users can not accept the invitation, " +
                "maximum number of active goals (max = 3)");
    }

    @Test
    void testRejectGoalInvitation_statusNotPending_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validationInvitation.rejectGoalInvitation(goalInvitationEntity));
    }
}