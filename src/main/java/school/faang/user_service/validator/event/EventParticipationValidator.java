package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventParticipationValidator {
    public void checkForNull(Long eventId, Long userId) {
        if (eventId == null) {
            throw new DataValidationException( "Event id is null" );
        }
        if (userId == null) {
            throw new DataValidationException( "User id is null" );
        }
    }

    public void checkForNull(Long eventId) {
        if (eventId == null) {
            throw new DataValidationException( "Event id is null" );
        }
    }
}
