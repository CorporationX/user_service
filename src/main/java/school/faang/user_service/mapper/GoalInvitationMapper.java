package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    GoalInvitationDto gInvitationToGIDTO(GoalInvitation createdInvitation);

    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(target = "goal", ignore = true)
    GoalInvitation gIDTOToGoalInvitation(GoalInvitationDto goalInvitationDto);
}
