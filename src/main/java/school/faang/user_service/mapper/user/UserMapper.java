package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Configuration
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "participatedEvents", target = "participatedEventIds", qualifiedByName = "mapEventsToEventIds")
    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    @Named("mapEventsToEventIds")
    default List<Long> mapEventsToEventIds(List<Event> participatedEvents) {
        return participatedEvents.stream()
                .map(Event::getId)
                .toList();
    }
}