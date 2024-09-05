package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;

import java.util.List;

public interface EventService {
    EventDto create(EventDto eventDto);
    EventDto getEvent(Long eventId);
    List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto);
    EventWithSubscribersDto updateEvent(EventDto eventDto);
    List<EventDto> getOwnedEvents(Long userId);
    List<EventDto> getParticipatedEvents(Long userId);
    void deleteEvent(Long eventId);

}
