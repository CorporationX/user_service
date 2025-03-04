package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring")
public interface GoalInvitationMapper {
    @Mapping(source = "inviterId", target = "inviter")
    @Mapping(source = "invitedUserId", target = "invited")
    @Mapping(source = "goalId", target = "goal")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    default User toInviter(long inviterId) {
        User user = new User();
        user.setId(inviterId);
        return user;
    }

    default Goal toGoal(long goalId) {
        Goal goal = new Goal();
        goal.setId(goalId);
        return goal;
    }
}
