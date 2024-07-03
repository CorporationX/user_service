package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
@Component
public interface GoalInvitationMapper {

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(source = "inviterId", target = "inviter.id")
    @Mapping(source = "invitedUserId", target = "invited.id")
    @Mapping(source = "goalId", target = "goal.id")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);
}
