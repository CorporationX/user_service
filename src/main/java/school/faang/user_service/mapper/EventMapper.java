package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", ignore = true)
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> eventList);

    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntity(EventDto eventDto);

    List<Event> toEntity(List<EventDto> eventDtoList);

    @Mapping(source = "id", target = "event_id")
    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "attendeesIdMapper")
    EventStartEvent toEventStartEvent(Event event);

    @Named("attendeesIdMapper")
    default List<Long> mapToAttendeeIds(List<User> users) {

        return users.stream().map( User::getId ).toList();

    }

}
