package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {

    @Mapping(target = "id", ignore = true)
    GoalInvitation toEntity(GoalInvitationDto dto);

    GoalInvitationDto toDto(GoalInvitation entity);
}