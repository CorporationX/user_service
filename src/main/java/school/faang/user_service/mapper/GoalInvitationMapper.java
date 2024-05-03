package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface GoalInvitationMapper {

    @Mapping(source = "inviterId", target = "inviter", qualifiedByName = "idToInviter")
    @Mapping(source = "invitedUserId", target = "invited", qualifiedByName = "idToInvited")
    @Mapping(source = "goalId", target = "goal", qualifiedByName = "goalIdToGoal")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);

    @Named("idToInviter")
    default User idToInviter(Long id) {
        User inviter = new User();
        inviter.setId(id);
        return inviter;
    }

    @Named("idToInvited")
    default User idToInvited(Long id) {
        User invited = new User();
        invited.setId(id);
        return invited;
    }
    @Named("goalIdToGoal")
    default Goal goalIdToGoal(Long id) {
        Goal goal = new Goal();
        goal.setId(id);
        return goal;
    }

    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);
}
