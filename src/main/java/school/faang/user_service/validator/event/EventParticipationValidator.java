package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventParticipationValidator {
    public void checkUserIdForNull(Long userId) {
        if (userId == null) {
            throw new DataValidationException( "User id is null" );
        }
    }

    public void checkEventIdForNull(Long eventId) {
        if (eventId == null) {
            throw new DataValidationException( "Event id is null" );
        }
    }
}
