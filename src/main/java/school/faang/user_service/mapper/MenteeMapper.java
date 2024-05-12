package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenteeMapper {
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "convertUserToId")
    @Mapping(source = "goals", target = "goalIds", qualifiedByName = "convertGoalToId")
    MenteeDto toDto(User mentee);

    User toEntity(MenteeDto menteeDTO);

    @Named("convertUserToId")
    default List<Long> convertUserToId(List<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

    @Named("convertGoalToId")
    default List<Long> convertGoalToId(List<Goal> goals) {
        return goals.stream().map(Goal::getId).collect(Collectors.toList());
    }

}
