package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

public interface EventService {

    EventDto create(EventDto eventDto);

    EventDto getEvent(long eventId);

    List<EventDto> getEventsByFilter(EventFilterDto filters);

    void deleteEvent(long eventId);

    void updateEvent(EventDto eventDto);

    List<EventDto> getOwnedEvents(long userId);

    List<EventDto> getParticipatedEvents(long userId);
}
