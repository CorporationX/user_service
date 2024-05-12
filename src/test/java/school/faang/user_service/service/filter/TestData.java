package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class TestData {

    public Stream<GoalInvitation> prepareGoalInvitationStream() {
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

        return Stream.of(goalInvitation);
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
