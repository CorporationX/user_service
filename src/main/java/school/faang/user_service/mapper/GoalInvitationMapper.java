package school.faang.user_service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.invitation.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

  @Mapping(source = "inviter.id", target = "inviterId")
  @Mapping(source = "invited.id", target = "invitedUserId")
  @Mapping(source = "goal.id", target = "goalId")
  GoalInvitationDto toDto(GoalInvitation goalInvitation);

  GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

  List<GoalInvitationDto> toDtoList(List<GoalInvitation> invitations);
}
