package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.goal.GoalDto;
import school.faang.user_service.model.dto.goal.GoalNotificationDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillToIds")
    GoalDto toDto(Goal goal);

    GoalNotificationDto toGoalNotificationDto(Goal goalDto);

    Goal toEntity(GoalDto goalDto);

    List<GoalDto> toDto(List<Goal> goals);

    @Named("mapSkillToIds")
    default List<Long> mapSkillToIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
