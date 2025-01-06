package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.response.CreateGoalResponseDto;
import school.faang.user_service.dto.response.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface GoalMapper {

    @Mapping(source = "mentor.id", target = "mentorId")
    CreateGoalResponseDto toCreateGoalResponseDto(Goal goal);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillIds")
    GoalDto toDto(Goal goal);

    List<GoalDto> toDto(List<Goal> goals);

    @Named("mapSkillIds")
    default List<Long> mapSkillIds(Set<Skill> skills) {
        return skills == null ? null : skills.stream()
                .map(Skill::getId)
                .toList();
    }

}
