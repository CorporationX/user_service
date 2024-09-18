package school.faang.user_service.filter.goalInvitation;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {

    boolean isApplicable(GoalInvitationFilterDto goalInvitationFilterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream,
                                 GoalInvitationFilterDto goalInvitationFilterDto);
}
