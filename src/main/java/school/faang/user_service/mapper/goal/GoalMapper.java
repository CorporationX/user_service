package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", conditionExpression = "java(usersExist(goal))", target = "userIds", qualifiedByName = "usersToIds")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveIds", qualifiedByName = "skillsToIds")
    GoalDto toDto(Goal goal);

    List<GoalDto> toDtos(List<Goal> goals);

    Goal toEntity(GoalDto goalDto);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<Skill> skillsToAchieve) {
        return skillsToAchieve.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("usersToIds")
    default List<Long> usersToIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }

    default boolean usersExist(Goal goal) {
        return goal.getUsers() != null && !goal.getUsers().isEmpty();
    }
}
