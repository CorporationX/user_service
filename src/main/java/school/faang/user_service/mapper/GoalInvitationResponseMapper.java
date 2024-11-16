package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalInvitationResponseDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationResponseMapper {
    GoalInvitation toEntity(GoalInvitationResponseDto invitationDto);

    GoalInvitationResponseDto toInvitationDTO(GoalInvitation goalInvitation);

}
