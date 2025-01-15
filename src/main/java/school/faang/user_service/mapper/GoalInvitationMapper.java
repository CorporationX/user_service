package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {

    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "inviter", ignore = true)
    @Mapping(target = "invited", ignore = true)
    void update(@MappingTarget GoalInvitation goalInvitation, GoalInvitationDto goalInvitationDto);


}
