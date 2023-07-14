package school.faang.user_service.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = Mappers.getMapper(EventMapper.class);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return eventMapper.eventToEventDto(event);
    }

    @Mapper
    public interface EventMapper {
        @Mapping(target = "relatedSkills", ignore = true)
        EventDto eventToEventDto(Event event);
    }
}
