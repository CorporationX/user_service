package school.faang.user_service.mapper;


import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mappings({
            @Mapping(source = "mentor.id", target = "mentorId"),
            @Mapping(source = "parent.id", target = "parentGoalId"),
            @Mapping(source = "users", target = "userIds", qualifiedByName = "mapUsersToUserIds"),
            @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToSkillIds"),
            @Mapping(source = "status", target = "status", qualifiedByName = "mapGoalStatus")
    })
    GoalDto toDto(Goal goal);
    @Mappings({
            @Mapping(source = "mentorId", target = "mentor.id"),
            @Mapping(source = "parentGoalId", target = "parent.id"),
            @Mapping(target = "users", ignore = true),
            @Mapping(target = "skillsToAchieve", ignore = true),
            @Mapping(source = "status", target = "status", qualifiedByName = "mapGoalStatus")
    })
    Goal toEntity(GoalDto goalDto);

    @Named("mapUsersToUserIds")
    default List<Long> mapUsersToUserIds(List<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

    @Named("mapSkillsToSkillIds")
    default List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    @Named("mapGoalStatus")
    default String mapGoalStatus(GoalStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("mapGoalStatus")
    default GoalStatus mapGoalStatus(String status) {
        return status != null ? GoalStatus.valueOf(status) : null;
    }
}
