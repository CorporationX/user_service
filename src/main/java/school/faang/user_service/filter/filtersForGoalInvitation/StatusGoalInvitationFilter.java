package school.faang.user_service.filter.filtersForGoalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

@Component
public class StatusGoalInvitationFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public List<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto) {
        return goalInvitationStream.filter(goalInvt -> goalInvt.getStatus().equals(filterDto.getStatus())).toList();
    }

}
