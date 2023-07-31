package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    RequestStatus PENDING = RequestStatus.PENDING;

    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "invitedUserId", source = "invited.id")
    @Mapping(target = "goalId", source = "goal.id")
    GoalInvitationDto toDto(GoalInvitation entity);

    @Mapping(target = "inviter.id", source = "inviterId")
    @Mapping(target = "invited.id", source = "invitedUserId")
    @Mapping(target = "goal.id", source = "goalId")
    @Mapping(target = "status", expression = "java(PENDING)")
    GoalInvitation toEntity(GoalInvitationDto dto);
}
