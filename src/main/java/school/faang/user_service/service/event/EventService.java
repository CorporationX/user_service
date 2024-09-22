package school.faang.user_service.service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;

    private Event getEventOrThrow(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with id: " + eventId));
    }

    public EventDto create(EventDto eventDto) {
        eventValidator.validateEventDto(eventDto);
        eventValidator.validateOwnerSkills(eventDto);
        Event event = eventMapper.toEvent(eventDto);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long eventId) {
        Event event = getEventOrThrow(eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        if (filters == null) {
            throw new DataValidationException("filters is null");
        }

        Stream<Event> events = eventRepository.findAll().stream();
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(events, filters))
                .map(eventMapper::toDto)
                .toList();

//        а такой метод не сработает?
//        return eventRepository.findAll().stream()
//                .map(eventMapper::eventToEventFilterDto)
//                .filter(event -> event.equals(filter))
//                .map(eventMapper::eventFilterDtoToEvent)
//                .map(eventMapper::eventToDto)
//                .toList();
    }

    public void deleteEvent(long eventId) {
        getEventOrThrow(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event eventFromDB = getEventOrThrow(eventDto.getId());
        boolean equalityId = Objects.equals(eventFromDB.getOwner().getId(), eventDto.getOwnerId());

        if (!equalityId) {
            throw new DataValidationException("the user is not the creator of the event with id: " + eventDto.getId());
        }

        eventValidator.validateEventDto(eventDto);
        eventValidator.validateOwnerSkills(eventDto);
        Event event = eventMapper.toEvent(eventDto);
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventsOwned = eventRepository.findAllByUserId(userId);

        return eventsOwned.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);

        return participatedEvents.stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
