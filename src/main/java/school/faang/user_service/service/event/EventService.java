package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
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
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;


    public EventDto createEvent(EventDto eventDto) {
        eventValidator.validateEventInService(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Invalid event id")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                eventStream = eventFilter.apply(eventStream, eventFilterDto);
            }
        }
        return eventMapper.toDto(eventStream.toList());
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateEventInService(eventDto);
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    public List<EventDto> getOwnedEvents(Long ownerId){
        List<Event> events = eventRepository.findAllByUserId(ownerId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getParticipatedEvents(Long userId){
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }
}
