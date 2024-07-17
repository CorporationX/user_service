package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "ownedEvents", target = "ownedEventsIds", qualifiedByName = "ownedEventsToIds")
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "menteesOrMentorsToIds")
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "menteesOrMentorsToIds")
    @Mapping(source = "setGoals", target = "setGoalsIds", qualifiedByName = "setGoalsOrGoalToIds")
    @Mapping(source = "goals", target = "goalsIds", qualifiedByName = "setGoalsOrGoalToIds")
    UserDto toDto(User user);

    @Mapping(target = "ownedEvents", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "goals", ignore = true)
    User toEntity(UserDto userDto);

    @Named("ownedEventsToIds")
    default List<Long> ownedEventsToIds(List<Event> ownedEvents) {
        if (ownedEvents == null) {
            return List.of();
        }
        return ownedEvents.stream().map(Event::getId).toList();
    }

    @Named("menteesOrMentorsToIds")
    default List<Long> menteesOrMentorsToIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("setGoalsOrGoalToIds")
    default List<Long> setGoalsOrGoalToIds(List<Goal> goals) {
        if (goals == null) {
            return List.of();
        }
        return goals.stream().map(Goal::getId).toList();
    }
}
