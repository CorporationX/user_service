package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(target = "userIds", expression = "java(mapUsers(goal))")
    @Mapping(target = "skillIds", expression = "java(mapSkills(goal))")
    GoalDto toGoalDto(Goal goal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    void update(@MappingTarget Goal goal, UpdateGoalDto updateGoalDto);

    default List<Long> mapUsers(Goal goal) {
        return goal.getUsers().stream()
                .map(User::getId)
                .toList();
    }

    default List<Long> mapSkills(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();
    }
}
