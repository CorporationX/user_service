package school.faang.user_service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GoalMapper {

    @Mapping(target = "status", expression = "java(school.faang.user_service.entity.goal.GoalStatus.ACTIVE)")
    Goal toEntity(CreateGoalDto dto);

    @Mapping(target = "id", source = "goalId")
    Goal toEntity(UpdateGoalDto dto);

    void update(Goal source, @MappingTarget Goal target);

    @Mapping(target = "parentGoalId", source = "parent.id")
    @Mapping(target = "userIds", source = "users", qualifiedByName = "mapUsersToIds")
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "mapSkillsToIds")
    GoalResponseDto toDto(Goal entity);

    List<GoalResponseDto> toDtos(List<Goal> entities);

    @Named("mapUsersToIds")
    default List<Long> mapUsersToIds(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
            .map(User::getId)
            .toList();
    }

    @Named("mapSkillsToIds")
    default List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream()
            .map(Skill::getId)
            .toList();
    }
}
