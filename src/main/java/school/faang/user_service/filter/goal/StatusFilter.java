package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class StatusFilter implements Filter<GoalInvitationDto, InvitationFilterDto> {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public void apply(Stream<GoalInvitationDto> goalInvitationDto, InvitationFilterDto filter) {
        goalInvitationDto.filter(invitation -> invitation.getStatus().equals(filter.getStatus()));
    }
}
