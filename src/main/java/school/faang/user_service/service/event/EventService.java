package school.faang.user_service.service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Data
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final List<EventFilter> eventFilters;
    private final EventValidation eventValidation;
    private final EventSaving eventSaving;

    private static final Logger LOGGER = LogManager.getLogger();

    public EventDto create(EventDto eventDto) {
        eventValidation.validateEventDto(eventDto);
        eventValidation.validateOwnerSkills(eventDto);
        return eventSaving.saveEventInDB(eventDto);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    LOGGER.warn("Event not found with id {}:", eventId);
                    return new NoSuchElementException("Event not found with id: " + eventId);
                });
        return eventMapper.eventToDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(events, filters))
                .map(eventMapper::eventToDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            LOGGER.warn("The deletion event was not found with id {}:", eventId);
            throw new NoSuchElementException("Event not found with id: " + eventId);
        }

        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        if (!eventRepository.findById(eventDto.getId()).isPresent()) {
            LOGGER.warn("The update event was not found with id {}:", eventDto.getId());
            throw new NoSuchElementException("Event not found with id: " + eventDto.getId());
        }

        eventValidation.validateEventDto(eventDto);
        eventValidation.validateOwnerSkills(eventDto);
        return eventSaving.saveEventInDB(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        if (eventRepository.findAllByUserId(userId).isEmpty()) {
            LOGGER.warn("Список событий пуст у user id {}:", userId);
            throw new NoSuchElementException("Список событий пуст у user id: " + userId);
        }

        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::eventToDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        if (eventRepository.findParticipatedEventsByUserId(userId).isEmpty()) {
            LOGGER.warn("Пользователь с id {}, не участвует ни в одном событии:", userId);
            throw new NoSuchElementException("Пользователь не участвует ни в одном событии, user id: " + userId);
        }

        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::eventToDto)
                .toList();
    }
}
