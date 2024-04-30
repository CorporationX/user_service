package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "users", target = "userIds", qualifiedByName = "userToId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "skillToId")
    GoalDto toDto(Goal goal);

    Goal toGoal(GoalDto goalDto);

    default List<Long> userToId(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    default List<Long> skillToId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
