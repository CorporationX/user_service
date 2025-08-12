package school.faang.user_service.service;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

public interface EventService {
    EventDto create(EventDto event);

    EventDto getEvent(long eventId);

    List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto);

    void deleteEvent(long id);

    EventDto updateEvent(EventDto event);

    List<EventDto> getOwnedEvents(long userId);

    List<EventDto> getParticipatedEvents(long userId);

    void deletePastEvents();
}
