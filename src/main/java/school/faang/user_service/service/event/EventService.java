package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        User owner = findUserById(eventDto.getOwnerId());
        Event event = eventMapper.toEntity(eventDto, userRepository);
        event.setOwner(owner);

        if (!hasRequiredSkills(owner, event)) {
            throw new DataValidationException("User hasn't required skills");
        }

        return eventMapper.toDto(eventRepository.save(event));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    private boolean hasRequiredSkills(User owner, Event event) {
        return owner.getSkills().containsAll(event.getRelatedSkills());
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for ID: " + eventId)));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .reduce(events,
                        ((eventStream, filter) -> filter.apply(eventStream, filters)),
                        ((eventStream, eventStream2) -> eventStream2)
                )
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        User owner = findUserById(eventDto.getOwnerId());
        Event event = eventMapper.toEntity(eventDto, userRepository);
        event.setOwner(owner);

        if (!hasRequiredSkills(owner, event)) {
            throw new DataValidationException("User hasn't required skills");
        }

        return eventMapper.toDto(eventRepository.save(event));
    }
}
