package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", uses = GoalMapperHelper.class)
public interface GoalMapper {

    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapUsersToIds")
    GoalDto goalToGoalDto(Goal goal);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "mentorId", target = "mentor", qualifiedByName = "mapMentorIdToUser")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapIdsToSkills")
    @Mapping(source = "userIds", target = "users", qualifiedByName = "mapIdsToUsers")
    @Mapping(target = "status", ignore = true) // Статус устанавливается в сервисе
    Goal goalDtoToGoal(GoalDto goalDto);

    void updateGoalFromDto(GoalDto goalDto, @MappingTarget Goal existingGoal);
}