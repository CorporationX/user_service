package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    Event create(EventDto eventDto);

    EventDto update(long eventId, UpdateEventDto updateEventDto);

    List<EventDto> getByFilters(EventFilterDto filters);

    void delete(long eventId);
}
