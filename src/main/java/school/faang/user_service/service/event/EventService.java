package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validate.event.EventValidate;

@Service
@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidate eventValidate;
    public EventDto create(EventDto eventDto) {
        eventValidate.checkThatUserHasNecessarySkills(eventDto);
        Event event = eventRepository.save(eventMapper.toEvent(eventDto));
        return eventMapper.toDto(event);
    }
}