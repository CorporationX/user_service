package school.faang.user_service.validator.event;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventValidator {

    public void validateEventId(Long eventId) {
        if (eventId == null) {
            throw new ValidationException("Event id can't be null");
        }
    }
}