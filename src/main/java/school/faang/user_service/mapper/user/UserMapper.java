package school.faang.user_service.mapper.user;

import com.json.student.PersonSchemaForUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.user.UserCsvDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "participatedEvents", target = "participatedEventIds", qualifiedByName = "mapEventsToEventIds")
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    @Named("mapEventsToEventIds")
    default List<Long> mapEventsToEventIds(List<Event> participatedEvents) {
        return participatedEvents.stream()
                .map(Event::getId)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "country.title", source = "country")
    UserCsvDto personToUserCsvDto(PersonSchemaForUser person);
    User toCsvEntity (UserCsvDto userCsvDto);
}