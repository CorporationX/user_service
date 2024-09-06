package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(source = "inviterId", target = "inviter.id")
    @Mapping(source = "invitedUserId", target = "invited.id")
    @Mapping(source = "goalId", target = "goal.id")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(source = "inviterId", target = "inviter.id")
    @Mapping(source = "invitedUserId", target = "invited.id")
    @Mapping(source = "goalId", target = "goal.id")
    List<GoalInvitation> toEntity(List<GoalInvitationDto> goalInvitationDto);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    List<GoalInvitationDto> toDto(List<GoalInvitation> goalInvitation);
}
