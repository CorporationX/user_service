package school.faang.user_service.filter.goalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalInvitationInviterIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto goalInvitationFilterDto) {
        return goalInvitationFilterDto.getInviterId() != null &&
                goalInvitationFilterDto.getInviterId() > 0;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream,
                                        GoalInvitationFilterDto goalInvitationFilterDto) {
        return goalInvitationStream.filter(goalInvitation -> goalInvitation.getInviter() != null &&
                Objects.equals(goalInvitation.getInviter().getId(), goalInvitationFilterDto.getInviterId()));
    }
}
