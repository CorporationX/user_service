package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_EVENT_EXCEPTION;

@Service
@AllArgsConstructor
@Setter
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private List<EventFilter> filters;
    private final EventServiceValidation eventServiceValidation;


    public EventDto create(EventDto event) {
        eventServiceValidation.checkOwnerSkills(event);

        return saveEvent(event);
    }

    public EventDto getEvent(Long eventId) {
        eventServiceValidation.checkEventPresence(eventId);

        return eventMapper.toDto(getEventById(eventId).orElseThrow(() -> new DataValidationException(NO_SUCH_EVENT_EXCEPTION.getMessage())));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        var allEvents = eventRepository.findAll();

        return filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .flatMap(eventFilter -> eventFilter.apply(allEvents, filter))
                .distinct()
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        eventServiceValidation.eventUpdateValidation(event);

        return saveEvent(event);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDtos(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDtos(eventRepository.findParticipatedEventsByUserId(userId));
    }

    private Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    private EventDto saveEvent(EventDto event) {
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(event)));
    }
}
