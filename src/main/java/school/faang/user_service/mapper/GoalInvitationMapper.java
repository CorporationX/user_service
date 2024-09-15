package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {
    GoalInvitationMapper INSTANCE = Mappers.getMapper(GoalInvitationMapper.class);
    GoalInvitation toEntity(GoalInvitationDto invitationDto);
}
