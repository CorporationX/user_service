package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "goalIds", target = "goals", qualifiedByName = "idListToGoalList")
    @Mapping(source = "participatedEventIds", target = "participatedEvents", qualifiedByName = "idListToEventList")
    @Mapping(source = "menteeIds", target = "mentees", qualifiedByName = "idListToUserList")
    User toEntity(UserDto userDto);

    @Mapping(source = "goals", target = "goalIds", qualifiedByName = "goalListToIdList")
    @Mapping(source = "participatedEvents", target = "participatedEventIds", qualifiedByName = "eventListToIdList")
    @Mapping(source = "mentees", target = "menteeIds", qualifiedByName = "userListToIdList")
    UserDto toDto(User user);

    @Named("idListToGoalList")
    default List<Goal> idToGoal(List<Long> idList){
        if(idList == null) return null;
        return idList.stream()
                .map(id -> Goal.builder().id(id).build())
                .toList();
    }
    @Named("goalListToIdList")
    default List<Long> goalListToIdList(List<Goal> goalList){
        if(goalList == null) return null;
        return goalList.stream()
                .map(Goal::getId)
                .toList();
    }

    @Named("idListToEventList")
    default List<Event> idToEventList(List<Long> idList){
        if(idList == null) return null;
        return idList.stream()
                .map(id -> Event.builder().id(id).build())
                .toList();
    }
    @Named("eventListToIdList")
    default List<Long> eventListToIdList(List<Event> eventList){
        if(eventList == null) return null;
        return eventList.stream()
                .map(Event::getId)
                .toList();
    }

    @Named("idListToUserList")
    default List<User> idListToUserList(List<Long> idList){
        if(idList == null) return null;
        return idList.stream()
                .map(id -> User.builder().id(id).build())
                .toList();
    }
    @Named("userListToIdList")
    default List<Long> userListToIdList(List<User> userList){
        if(userList == null) return null;
        return userList.stream()
                .map(User::getId)
                .toList();
    }
}
