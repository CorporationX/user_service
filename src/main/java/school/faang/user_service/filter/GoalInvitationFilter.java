package school.faang.user_service.filter;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {
    boolean isApplicable(GoalInvitationFilterDto vacancyFilterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> vacancyStream, GoalInvitationFilterDto vacancyFilterDto);
}
