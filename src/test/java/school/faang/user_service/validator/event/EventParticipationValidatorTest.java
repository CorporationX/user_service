package school.faang.user_service.validator.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationValidatorTest {
    private final long eventId = 1L;
    private final long userId = 1L;

    @InjectMocks
    private EventParticipationValidator eventParticipationValidator;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Test
    public void validateParticipantRegistered_Registered() {
        User participant = createUser();

        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(participant);

        assertThrows(EventParticipationRegistrationException.class, () ->
                eventParticipationValidator.validateParticipationRegistered(eventId, userId));
    }

    @Test
    public void validateParticipantRegistered_NotRegistered() {
        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(null);

        assertDoesNotThrow(() -> eventParticipationValidator.validateParticipationRegistered(eventId, userId));

        verify(eventParticipationRepository).findParticipantByIdAndEventId(eventId, userId);
    }

    @Test
    public void validateParticipationNotRegistered_Registered() {
        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(null);

        assertThrows(EventParticipationRegistrationException.class, () ->
                eventParticipationValidator.validateParticipationNotRegistered(eventId, userId));
    }

    @Test
    public void validateParticipationNotRegistered_NotRegistered() {
        User participant = createUser();

        when(eventParticipationRepository.findParticipantByIdAndEventId(eventId, userId)).thenReturn(participant);

        assertDoesNotThrow(() -> eventParticipationValidator.validateParticipationNotRegistered(eventId, userId));

        verify(eventParticipationRepository).findParticipantByIdAndEventId(eventId, userId);
    }

    @Test
    public void checkEventExists_Exists() {
        when(eventParticipationRepository.eventExistsById(eventId)).thenReturn(true);

        assertDoesNotThrow(() -> eventParticipationValidator.checkEventExists(eventId));

        verify(eventParticipationRepository).eventExistsById(eventId);
    }

    @Test
    public void checkEventExists_NotExists() {
        when(eventParticipationRepository.eventExistsById(eventId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                eventParticipationValidator.checkEventExists(eventId));
    }

    @Test
    public void checkUserExists_Exists() {
        when(eventParticipationRepository.userExistsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> eventParticipationValidator.checkUserExists(userId));

        verify(eventParticipationRepository).userExistsById(userId);
    }

    @Test
    public void checkUserExists_UserNotExists() {
        when(eventParticipationRepository.userExistsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                eventParticipationValidator.checkUserExists(userId));
    }

    private User createUser() {
        return new User();
    }
}