package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.GoalInvitationDto;
import school.faang.user_service.model.entity.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

}