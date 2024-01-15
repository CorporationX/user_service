package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "invitations", target = "invitationsIds", qualifiedByName = "mapToInvitationsIds")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapToUserIds")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveIds", qualifiedByName = "mapToSkillsToAchieveIds")
    GoalDto toDto(Goal goal);

    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);


    @Named("mapToUserIds")
    default List<Long> mapToUserIds(List<User> users){
        return users.stream().map(User::getId).toList();
    }
    @Named("mapToInvitationsIds")
    default List<Long> mapToInvitationsIds(List<GoalInvitation> invitations){
        return invitations.stream().map(GoalInvitation::getId).toList();
    }
    @Named("mapToSkillsToAchieveIds")
    default List<Long> mapToSkillsToAchieveIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
