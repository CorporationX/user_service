package school.faang.user_service.mapper.goal;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(target = "skillIds", source = "skillsToAchieve", qualifiedByName = "skillsToIds")
    @Mapping(target = "userIds", source = "users", qualifiedByName = "usersToIds")
    @Mapping(target = "parentId", source = "parent.id")
    GoalDto toDto(Goal goal);


    @Mapping(target = "skillsToAchieve", source = "skillIds", qualifiedByName = "idsToSkills")
    @Mapping(target = "users", source = "userIds", qualifiedByName = "idsToUsers")
    @Mapping(target = "parent.id", source = "parentId")
    Goal toEntity(GoalDto goal);

    List<GoalDto> goalListToDto(List<Goal> list);

    @Named("skillsToIds")
    default List<Long> mapSkillsToIds(List<Skill> value) {
        return value.stream().map(Skill::getId).toList();
    }

    @Named("usersToIds")
    default List<Long> mapUsersToIds(List<User> value) {
        return value.stream().map(User::getId).toList();
    }

    @Named("idsToSkills")
    default List<Skill> mapIdsToSkills(List<Long> value) {
        if (value.equals(null)) {
            return null;
        }
        List<Skill> res = new ArrayList<>(value.size());
        for (Long id : value) {
            Skill skill = new Skill();
            skill.setId(id);
            res.add(skill);
        }
        return res;

    List<GoalDto> goalsToDtos(List<Goal> list);

    @Named("skillsToIds")
    default List<Long> map(List<Skill> value) {
        return value.stream().map(Skill::getId).toList();
    }

    @Named("idsToUsers")
    default List<User> mapIdsToUsers(List<Long> value) {
        if (value.equals(null)) {
            return null;
        }
        List<User> res = new ArrayList<>(value.size());
        for (Long id : value) {
            User user = new User();
            user.setId(id);
            res.add(user);
        }
        return res;
    }
}
