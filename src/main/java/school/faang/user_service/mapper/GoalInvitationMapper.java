package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {
    @Mapping(source = "dto.inviterId", target = "inviter.id")
    @Mapping(source = "dto.invitedUserId", target = "invited.id")
    @Mapping(source = "dto.goalId", target = "goal.id")
    GoalInvitation toEntity(GoalInvitationDto dto);

    @Mapping(source = "entity.inviter.id", target = "inviterId")
    @Mapping(source = "entity.invited.id", target = "invitedUserId")
    @Mapping(source = "entity.goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation entity);

    List<GoalInvitationDto> toDtoList(List<GoalInvitation> entityList);
}
