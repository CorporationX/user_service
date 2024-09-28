package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface EventMapper {
    @Mapping(source = "attendees", target = "attendeeEmails", qualifiedByName = "map")
    EventDto toDto(Event event);

    @Named("map")
    default List<String> map(List<User> users) {
        return users.stream()
                .map(User::getEmail)
                .toList();
    }
}
