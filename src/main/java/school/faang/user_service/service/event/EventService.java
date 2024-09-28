package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final EventMapper mapper;

    public EventDto getEvent(long id) {
        return mapper.toDto(repository.getReferenceById(id));
    }

    public void setCalendarEventId(long eventId, String calendarEventId) {
        Event event = repository.getReferenceById(eventId);
        event.setCalendarEventId(calendarEventId);
        repository.save(event);
    }
}
