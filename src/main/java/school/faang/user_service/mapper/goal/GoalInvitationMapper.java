package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.goal.GoalInvitationDto;
import school.faang.user_service.model.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(target = "inviter.id", source = "inviterId")
    @Mapping(target = "invited.id", source = "invitedUserId")
    @Mapping(target = "goal.id", source = "goalId")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "invitedUserId", source = "invited.id")
    @Mapping(target = "goalId", source = "goal.id")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    List<GoalInvitationDto> toDto(List<GoalInvitation> goalInvitations);
}