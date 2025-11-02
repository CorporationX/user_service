package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GoalInvitationMapper {

    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toGoalInvitationDto(GoalInvitation invitation);
}