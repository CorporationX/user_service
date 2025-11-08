package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;

import java.util.List;

public interface EventService {
    EventDto createEvent(CreateEventDto dto);

    EventDto updateEvent(long eventId, UpdateEventDto dto);

    List<EventDto> getEventByFilters(EventFilterDto filters);

    void deleteEvent(long eventId);
}
