package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public EventDto getEvent(long id) {
        Event entity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        return eventMapper.toDto(entity);
    }
}
