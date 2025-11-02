package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(target = "userIds", expression = "java(getUserIds(goal))")
    GoalDto toGoalDto(Goal goal);

    @Mapping(target = "mentor", ignore = true)
    void update(UpdateGoalDto dto, @MappingTarget Goal entity);

    default List<Long> getUserIds(Goal goal) {
        return goal.getUsers().stream().map(User::getId).toList();
    }
}
