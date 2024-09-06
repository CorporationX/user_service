package school.faang.user_service.filter.goalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalInvitationInvitedIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto goalInvitationFilterDto) {
        return goalInvitationFilterDto.getInvitedId() != null &&
                goalInvitationFilterDto.getInvitedId() > 0;
    }

    @Override
    public Stream<GoalInvitation> apply(List<GoalInvitation> goalInvitationStream,
                                        GoalInvitationFilterDto goalInvitationFilterDto) {
        return goalInvitationStream.stream().filter(goalInvitation ->
                Objects.equals(goalInvitation.getInvited().getId(), goalInvitationFilterDto.getInvitedId()));
    }
}