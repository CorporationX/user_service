package school.faang.user_service.mapper.goal;

import org.mapstruct.Named;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.InjectionStrategy;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(target = "goal", source = "id", qualifiedByName = "mapGoal")
    @Mapping(target = "inviter", source = "inviterId", qualifiedByName = "mapInviter")
    @Mapping(target = "invited", source = "invitedUserId", qualifiedByName = "mapInvitedUser")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "invitedUserId", source = "invited.id")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Named("mapGoal")
    default Goal mapGoal(Long goalId) {
        if (goalId == null) {
            return null;
        }
        return Goal.builder().id(goalId).build();
    }

    @Named("mapInviter")
    default User mapInviter(Long inviterId) {
        if (inviterId == null) {
            return null;
        }
        return User.builder().id(inviterId).build();
    }

    @Named("mapInvitedUser")
    default User mapInvitedUser(Long invitedUserId) {
        if (invitedUserId == null) {
            return null;
        }
        return User.builder().id(invitedUserId).build();
    }
}
