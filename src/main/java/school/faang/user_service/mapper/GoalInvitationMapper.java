package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);
    GoalInvitationDto toDto(GoalInvitation goalInvitation);
}
