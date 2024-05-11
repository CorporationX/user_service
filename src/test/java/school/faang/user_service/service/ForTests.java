package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.ArrayList;
import java.util.List;

public class ForTests {
    public GoalInvitationDto setupForCreateInvitation() {
        return new GoalInvitationDto(25L, 25L, 20L, 30L, RequestStatus.PENDING);
    }

    public GoalInvitation setupForAcceptAndRejectGoalInvitationAndForGetInvitations() {

        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);

        Goal goal = new Goal();
        goal.setId(2L);

        goalInvitation.setGoal(goal);

        User invited = new User();
        invited.setGoals(new ArrayList<>());
        invited.setUsername("Mike");
        invited.setId(2L);

        invited.setSetGoals(new ArrayList<>(List.of(
                new Goal(),
                goal
        )));

        User inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("John");

        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitation.setInvited(invited);
        goalInvitation.setInviter(inviter);
        return goalInvitation;
    }
}
