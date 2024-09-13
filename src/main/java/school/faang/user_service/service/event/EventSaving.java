package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventSaving {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    public EventDto saveEventInDB(EventDto eventDto) {
        Event event = eventMapper.eventDtoToEvent(eventDto);
        Event eventInDB = eventRepository.save(event);
        return eventMapper.eventToDto(eventInDB);
    }
}
