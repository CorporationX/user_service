package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalStatusDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

@Mapper(componentModel = "spring", uses = GoalInvitationMapper.class, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(CreateGoalDto dto);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    @Mapping(target = "status", source = "status")
    Goal toEntity(UpdateGoalDto dto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "usersId")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveIds")
    @Mapping(source = "status", target = "status")
    GoalResponseDto toResponseDto(Goal goal);

    default Long map(User user) {
        return user.getId();
    }

    default Long map(Skill skill) {
        return skill.getId();
    }

    default GoalStatus map(GoalStatusDto statusDto) {
        return GoalStatus.valueOf(statusDto.name());
    }

    default GoalStatusDto map(GoalStatus status) {
        return GoalStatusDto.valueOf(status.name());
    }
}
