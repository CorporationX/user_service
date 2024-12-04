package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final EventDtoValidator eventValidator;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        eventValidator.validate(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userService.findById(eventDto.getOwnerId()));
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is no event with this id"));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilters(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));

        return eventMapper.toListDto(events.toList());
    }

    public void deleteEvent(long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new DataValidationException("There is no event with this id");
        }
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validate(eventDto);
        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException("There is no event with this id"));
        Event updatedEvent = eventMapper.toEntity(eventDto);
        updatedEvent.setOwner(event.getOwner());
        eventRepository.save(updatedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findParticipatedEventsByUserId(userId));
    }
}
