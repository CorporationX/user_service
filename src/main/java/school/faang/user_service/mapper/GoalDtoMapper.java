package school.faang.user_service.mapper;

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
public interface GoalDtoMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "usersId", qualifiedByName = "usersToIds")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveId", qualifiedByName = "skillsToIds")
    GoalDto toDto(Goal goal);

    List<GoalDto> toDtos(List<Goal> goals);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Named("skillsToIds")
    default List<Long> toIds(List<Skill> skillsToAchieve) {
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
}
