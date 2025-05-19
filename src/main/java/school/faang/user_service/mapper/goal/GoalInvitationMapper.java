package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface GoalInvitationMapper {

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toGoalInvitationDto(GoalInvitation goalInvitation);

    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GoalInvitation toGoalInvitation(User inviter, User invited, Goal goal);

    List<GoalInvitationDto> toGoalInvitations(List<GoalInvitation> goalInvitationsDto);
}
