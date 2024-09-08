package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventExistsException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.exception.UserExistsException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventValidatorTest")
public class EventValidatorTest {
    long eventId = 1L;
    long userId = 1L;

    @InjectMocks
    private EventValidator eventValidator;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Test
    @DisplayName("validateParticipantRegistered_Registered")
    public void validateParticipantRegistered_Registered() {
        User participant = setUpCreateUser();

        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(participant);

        assertThrows(EventParticipationRegistrationException.class, () ->
                eventValidator.validateParticipationRegistered(eventId, userId));
    }

    @Test
    @DisplayName("validateParticipantRegistered_NotRegistered")
    public void validateParticipantRegistered_NotRegistered() {
        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(null);

        boolean result = eventValidator.validateParticipationRegistered(eventId, userId);

        assertEquals(false, result);
    }

    @Test
    @DisplayName("validateParticipationNotRegistered_Registered")
    public void validateParticipationNotRegistered_Registered() {
        User participant = setUpCreateUser();

        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(participant);

        boolean result = eventValidator.validateParticipationNotRegistered(eventId, userId);

        assertEquals(false, result);
    }

    @Test
    @DisplayName("validateParticipationNotRegistered_NotRegistered")
    public void validateParticipationNotRegistered_NotRegistered() {
        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(null);

        assertThrows(EventParticipationRegistrationException.class, () ->
                eventValidator.validateParticipationNotRegistered(eventId, userId));
    }

    @Test
    @DisplayName("checkEventExists_Exists")
    public void checkEventExists_Exists() {
        setUpCheckEventExists(true);

        boolean result = eventValidator.checkEventExists(eventId);

        assertEquals(true, result);
    }

    @Test
    @DisplayName("checkEventExists_NotExists")
    public void checkEventExists_NotExists() {
        setUpCheckEventExists(false);

        assertThrows(EventExistsException.class, () ->
                eventValidator.checkEventExists(eventId));
    }

    @Test
    @DisplayName("checkUserExists_Exists")
    public void checkUserExists_Exists() {
        setUpCheckUserExists(true);

        boolean result = eventValidator.checkUserExists(userId);

        assertEquals(true, result);
    }

    @Test
    @DisplayName("checkUserExists_UserNotExists")
    public void checkUserExists_UserNotExists() {
        setUpCheckUserExists(false);

        assertThrows(UserExistsException.class, () ->
                eventValidator.checkUserExists(userId));
    }

    private void setUpCheckUserExists(boolean isUserExists) {
        when(eventParticipationRepository.userExistsById(userId)).thenReturn(isUserExists);
    }

    private void setUpCheckEventExists(boolean isEventExists) {
        when(eventParticipationRepository.eventExistsById(eventId)).thenReturn(isEventExists);
    }

    private User setUpCreateUser() {
        User user = new User();
        return user;
    }
}
