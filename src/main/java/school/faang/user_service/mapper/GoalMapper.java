package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDTO;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapUserToId")
    @Mapping(source = "skillsToAchieve", target = "skillToAchieveIds", qualifiedByName = "mapAchieveToId")
    GoalDTO toDto(Goal goal);

    Goal toEntity(GoalDTO dto);

    List<GoalDTO> toDtoList(List<Goal> list);

    @Named("mapUserToId")
    default List<Long> mapUserToId(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("mapAchieveToId")
    default List<Long> mapAchieveToId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }
}
