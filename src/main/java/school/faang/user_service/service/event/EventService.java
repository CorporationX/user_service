package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

public interface EventService {
    List<EventDto> getParticipatedEvents(long userId);

    List<EventDto> getOwnedEvents(long userId);

    EventDto updateEvent(EventDto event);

    EventDto deleteEvent(long eventId);

    List<EventDto> getEventsByFilter(EventFilterDto filter);

    EventDto getEvent(long eventId);

    EventDto create(EventDto event);
}
