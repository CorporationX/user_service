package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {
    GoalInvitationMapper INSTANCE = Mappers.getMapper(GoalInvitationMapper.class);

    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(source = "goalId", target = "goal.id")
    @Mapping(source = "inviterId", target = "inviter.id")
    @Mapping(source = "invitedUserId", target = "invited.id")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    List<GoalInvitationDto> toDtoList(List<GoalInvitation> goalInvitations);
}
