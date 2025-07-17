package school.faang.user_service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.SkillServiceImpl;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.service.UserServiceImpl;

@Mapper(componentModel = "spring", uses = {SkillServiceImpl.class})
public abstract class GoalMapper {
    @Autowired
    protected UserServiceImpl userService;

    //todo warning: Unmapped target properties: "parent, createdAt, updatedAt, invitations, users".
    @Mapping(source = "deadline", target = "deadline", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "mentorId", target = "mentor", qualifiedByName = "mapIdToUser")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapSkillIdsToSkillList")
    public abstract Goal toEntity(GoalDto goalDto, @Context GoalServiceImpl goalService);

    //todo warning: Unmapped target properties: "skillIds, subGoals".
    @Mapping(source = "mentor.id", target = "mentorId")
    public abstract GoalDto toDto(Goal goal);

    @Named("mapIdToUser")
    protected User getUserOrEmpty(Long userId) {
        return userId == null ? new User() : userService.getUserEntityById(userId);
    }
}
