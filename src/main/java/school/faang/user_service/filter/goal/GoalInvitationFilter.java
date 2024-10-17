package school.faang.user_service.filter.goal;

import school.faang.user_service.model.dto.goal.InvitationFilterDto;
import school.faang.user_service.model.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {

    boolean isApplicable(InvitationFilterDto filter);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filter);
}