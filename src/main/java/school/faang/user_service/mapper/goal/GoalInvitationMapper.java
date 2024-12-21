package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(target = "id", source = "invitation.id")
    @Mapping(target = "inviterId", source = "invitation.inviter.id")
    @Mapping(target = "invitedUserId", source = "invitation.invited.id")
    @Mapping(target = "goalId", source = "invitation.goal.id")
    @Mapping(target = "status", source = "invitation.status")
    GoalInvitationDto toDto(GoalInvitation invitation);

    @Mapping(target = "id", source = "invitationDto.id")
    @Mapping(target = "inviter.id", source = "invitationDto.inviterId")
    @Mapping(target = "invited.id", source = "invitationDto.invitedUserId")
    @Mapping(target = "goal.id", source = "invitationDto.goalId")
    @Mapping(target = "status", source = "invitationDto.status")
    GoalInvitation toEntity(GoalInvitationDto invitationDto);

    List<GoalInvitationDto> toDto(List<GoalInvitation> invitations);
}
