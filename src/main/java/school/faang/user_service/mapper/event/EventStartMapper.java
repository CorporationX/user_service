package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventStartMapper {

    @Mapping(target = "attendeeIds", source = "attendees", qualifiedByName = "toAttendeeIds")
    EventStartDto toDto(Event event);

    @Named("toAttendeeIds")
    default List<Long> toAttendeeIds(List<User> attendees) {
        return attendees.stream()
                .map(User::getId)
                .toList();
    }
}
