package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventServiceValidator {
    private final EventRepository eventRepository;

    public void validateRequiredSkills(User owner, Event event) {
        if (event.getRelatedSkills() != null &&
                (owner.getSkills() == null || !owner.getSkills().containsAll(event.getRelatedSkills()))
                ) {
            throw new DataValidationException("User hasn't required skills");
        }
    }

    public void validateEventId(long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event with id " + eventId + " doesn't exist"));
    }
}