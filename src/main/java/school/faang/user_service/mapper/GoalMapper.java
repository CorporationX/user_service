package school.faang.user_service.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "getIdSkills")
    @Mapping(source = "parent.id", target = "parentId")
    GoalDto toDto(Goal goal);

    @Mapping(target = "skillsToAchieve", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Named("getIdSkills")
    default List<Long> getIdSkills(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId).toList();
    }
}
