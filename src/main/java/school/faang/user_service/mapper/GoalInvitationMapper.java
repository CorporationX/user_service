package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationResponseDto;
import school.faang.user_service.dto.goal.RequestStatusDto;
import school.faang.user_service.model.RequestStatus;
import school.faang.user_service.model.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "status", target = "status")
    GoalInvitationResponseDto toResponseDto(GoalInvitation goalInvitation);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    List<GoalInvitationDto> toDtoList(List<GoalInvitation> goalInvitations);

    List<GoalInvitation> toEntityList(List<GoalInvitationDto> goalInvitationDto);

    RequestStatusDto toRequestStatusDto(RequestStatus goalInvitation);
}
