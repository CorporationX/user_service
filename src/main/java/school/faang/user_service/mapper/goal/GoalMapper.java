package school.faang.user_service.mapper.goal;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapUsersToIds")
    GoalDto goalToGoalDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "status", ignore = true) // Статус устанавливается в сервисе
    Goal goalDtoToGoal(GoalDto goalDto);

    void updateGoalFromDto(@MappingTarget Goal existingGoal, GoalDto goalDto);

    @Named("mapSkillsToIds")
    static List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills == null ? null : skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("mapUsersToIds")
    static List<Long> mapUsersToIds(List<User> users) {
        return users == null ? null : users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}