package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class Data {
    private InvitationFilterDto invitationFilterDto;

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
        invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setInvitedId(2L);
        invitationFilterDto.setInviterId(1L);
        invitationFilterDto.setInviterNamePattern("John");
        invitationFilterDto.setInvitedNamePattern("Mike");
        invitationFilterDto.setStatus(RequestStatus.ACCEPTED);
        return invitationFilterDto;
    }
}
