package school.faang.user_service.service.event;

import school.faang.user_service.entity.event.Event;

import java.util.List;
import school.faang.user_service.dto.event.EventDto;

public interface EventService {

    List<Event> getPastEvents();

    void deleteEventsByIds(List<Long> ids);

    EventDto getEvent(long id);

    void startEvent(long eventId);
}
