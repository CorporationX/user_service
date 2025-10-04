package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;

import java.util.List;

public interface EventService {
    EventDto create(EventDto eventDto);

    EventDto update(long eventId, UpdateEventDto updateEventDto);

    List<EventDto> getByFilters(EventFilterDto filters);

    void delete(long eventId);
}
