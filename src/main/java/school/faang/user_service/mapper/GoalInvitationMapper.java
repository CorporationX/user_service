package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(target = "id", source = "invitation.id")
    @Mapping(target = "goal", source = "goal")
    @Mapping(target = "inviter", source = "inviter")
    @Mapping(target = "invited", source = "invited")
    @Mapping(target = "status", source = "invitation.status")
    @Mapping(target = "createdAt", source = "invitation.createdAt")
    @Mapping(target = "updatedAt", source = "invitation.updatedAt")
    GoalInvitation toEntity(GoalInvitationDto invitation, Goal goal, User inviter, User invited);

    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "invitedUserId", source = "invited.id")
    @Mapping(target = "goalId", source = "goal.id")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    List<GoalInvitationDto> toDtos(List<GoalInvitation> goalInvitations);
}
