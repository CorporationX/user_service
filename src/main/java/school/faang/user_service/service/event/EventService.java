package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validation.event.EventValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;
    private final UserValidator userValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.validateUserHasRequiredSkills(eventDto);
        Event savedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long eventId) {
        eventValidator.validateEventExistsById(eventId);
        Event searchedEvent = eventRepository.findById(eventId).get(); //validateEventExistsById выкинет ошибку, если там null
        return eventMapper.toDto(searchedEvent);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toDto(events);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateUserHasRequiredSkills(eventDto);
        eventValidator.validateEventExistsById(eventDto.getId());
        Event event = eventRepository.findById(eventDto.getId()).get(); //validateEventExistsById выкинет ошибку, если там null
        eventValidator.validateUserIsOwnerOfEvent(event.getOwner(), eventDto);

        Event updatedAndSavedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedAndSavedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        userValidator.validateUserExistsById(userId);
        List<Event> userOwnedEvents = eventRepository.findAllByUserId(userId);
        return eventMapper.toDto(userOwnedEvents);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        userValidator.validateUserExistsById(userId);
        List<Event> userParticipatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDto(userParticipatedEvents);
    }
}
