package school.faang.user_service.service.goal.invitation.filter;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilterService {

    Stream<GoalInvitation> applyFilters(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filterDto);
}
