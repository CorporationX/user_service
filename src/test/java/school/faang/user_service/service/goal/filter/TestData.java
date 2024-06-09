package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestData {

    public List<GoalInvitation> prepareGoalInvitationList() {
        GoalInvitation goalInvitation = new GoalInvitation();

        User invited = new User();
        invited.setUsername("Mike");
        invited.setId(2L);

        User inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("John");

        goalInvitation.setStatus(RequestStatus.ACCEPTED);
        goalInvitation.setInvited(invited);
        goalInvitation.setInviter(inviter);

        GoalInvitation secondGoalInvitation = new GoalInvitation();

        User secondInvited = new User();
        secondInvited.setUsername("Mike");
        secondInvited.setId(2L);

        User secondInviter = new User();
        secondInviter.setId(1L);
        secondInviter.setUsername("John");

        secondGoalInvitation.setStatus(RequestStatus.ACCEPTED);
        secondGoalInvitation.setInvited(secondInvited);
        secondGoalInvitation.setInviter(secondInviter);

        GoalInvitation thirdGoalInvitation = new GoalInvitation();

        User thirdInviter = new User();
        thirdInviter.setUsername("Jessica");
        thirdInviter.setId(33L);

        User thirdInvited = new User();
        thirdInvited.setId(44L);
        thirdInvited.setUsername("Mike");

        thirdGoalInvitation.setStatus(RequestStatus.ACCEPTED);
        thirdGoalInvitation.setInvited(thirdInviter);
        thirdGoalInvitation.setInviter(thirdInvited);

        return List.of(thirdGoalInvitation, goalInvitation, secondGoalInvitation);
    }

    public InvitationFilterDto prepareInvitationFilterDto() {
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setInvitedId(2L);
        invitationFilterDto.setInviterId(1L);
        invitationFilterDto.setInviterNamePattern("John");
        invitationFilterDto.setInvitedNamePattern("Mike");
        invitationFilterDto.setStatus(RequestStatus.ACCEPTED);
        return invitationFilterDto;
    }

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
