package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto create(EventDto event) {
        eventValidator.validateOwnerSkills(event);
        Event eventEntity = eventMapper.toEntity(event);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    /*public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter)
        }
    }*/
}
