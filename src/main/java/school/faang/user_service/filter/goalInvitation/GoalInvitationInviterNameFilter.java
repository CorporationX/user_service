package school.faang.user_service.filter.goalInvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
class GoalInvitationInviterNameFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto goalInvitationFilterDto) {
        return goalInvitationFilterDto.getInviterNamePattern() != null &&
                !goalInvitationFilterDto.getInviterNamePattern().isBlank();
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream,
                                        GoalInvitationFilterDto goalInvitationFilterDto) {
        return goalInvitationStream.filter(goalInvitation -> goalInvitation.getInviter() != null &&
                goalInvitation.getInviter().getUsername().contains(goalInvitationFilterDto.getInviterNamePattern()));
    }
}
