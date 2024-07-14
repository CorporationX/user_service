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
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = MapperMethodUserDtoToUser.class)
public interface UserMapper {
    @Mapping(source = "ownedEvents", target = "ownedEventsIds", qualifiedByName = "ownedEventsToIds")
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "menteesOrMentorsToIds")
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "menteesOrMentorsToIds")
    @Mapping(source = "setGoals", target = "setGoalsIds", qualifiedByName = "setGoalsOrGoalToIds")
    @Mapping(source = "goals", target = "goalsIds", qualifiedByName = "setGoalsOrGoalToIds")
    UserDto toDto(User user);

    @Mapping(source = "ownedEventsIds", target = "ownedEvents", qualifiedByName = "idsToOwnedEvents")
    @Mapping(source = "menteesIds", target = "mentees", qualifiedByName = "idsToMenteesOrMentors")
    @Mapping(source = "mentorsIds", target = "mentors", qualifiedByName = "idsToMenteesOrMentors")
    @Mapping(source = "goalsIds", target = "goals", qualifiedByName = "idsToSetGoalsOrGoal")
    User toEntity(UserDto userDto);

    @Named("ownedEventsToIds")
    default List<Long> ownedEventsToIds(List<Event> ownedEvents) {
        return ownedEvents.stream().map(Event::getId).toList();
    }

    @Named("menteesOrMentorsToIds")
    default List<Long> menteesOrMentorsToIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("setGoalsOrGoalToIds")
    default List<Long> setGoalsOrGoalToIds(List<Goal> users) {
        return users.stream().map(Goal::getId).toList();
    }
}
