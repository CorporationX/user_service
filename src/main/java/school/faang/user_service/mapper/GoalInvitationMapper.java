package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {
    @Mapping(source = "inviterId", target = "inviter")
    @Mapping(source = "invitedUserId", target = "invited")
    @Mapping(source = "goalId", target = "goal")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(target = "inviterId", source = "inviter")
    @Mapping(target = "invitedUserId", source = "invited")
    @Mapping(target = "goalId", source = "goal")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    List<GoalInvitationDto> toDtoList(List<GoalInvitation> goalInvitations);

    default Long userToId(User user) {
        return user.getId();
    }

    default Long goalToId(Goal goal) {
        return goal.getId();
    }

    default User idToUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    default Goal idToGoal(Long id) {
        Goal goal = new Goal();
        goal.setId(id);
        return goal;
    }
}
