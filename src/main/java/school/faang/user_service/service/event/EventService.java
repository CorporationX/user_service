package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;

    public EventDto create(EventDto eventDto) {
        User owner = userService.findUserById(eventDto.getOwnerId());
        Event event = eventMapper.toEntity(eventDto, userService);
        event.setOwner(owner);

        if (!hasRequiredSkills(owner, event)) {
            throw new DataValidationException("User hasn't required skills");
        }

        return eventMapper.toDto(eventRepository.save(event));
    }

    private boolean hasRequiredSkills(User owner, Event event) {
        return owner.getSkills().containsAll(event.getRelatedSkills());
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for ID: " + eventId)));
    }
}
