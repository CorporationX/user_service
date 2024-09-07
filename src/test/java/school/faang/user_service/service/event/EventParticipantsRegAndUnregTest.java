package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.EventExistsException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.exception.UserExistsException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventParticipationServiceTest")
public class EventParticipantsRegAndUnregTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventValidator eventValidator;

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;
    private final long eventId = 1L;
    private final long userId = 1L;

    @Test
    @DisplayName("testRegisterParticipant")
    public void testRegisterParticipant() {
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(1)).validateUser(userId);
        verify(eventValidator, times(1)).validateParticipationRegistered(eventId, userId);
        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    @DisplayName("testRegisterParticipant_EventNotExists")
    public void testRegisterParticipant_EventNotExists() {
        when(eventValidator.validateEvent(eventId)).thenThrow(new EventExistsException("Event not exists"));
        EventExistsException exception = assertThrows(EventExistsException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(0)).validateUser(userId);
        verify(eventValidator, times(0)).validateParticipationRegistered(eventId, userId);
        verify(eventParticipationRepository, times(0)).register(eventId, userId);
        assertEquals("Event not exists", exception.getMessage());
    }

    @Test
    @DisplayName("testRegisterParticipant_UserNotExists")
    public void testRegisterParticipant_UserNotExists() {
        when(eventValidator.validateUser(userId)).thenThrow(new UserExistsException("User not exists"));
        UserExistsException exception = assertThrows(UserExistsException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(1)).validateUser(userId);
        verify(eventValidator, times(0)).validateParticipationRegistered(eventId, userId);
        verify(eventParticipationRepository, times(0)).register(eventId, userId);
        assertEquals("User not exists", exception.getMessage());
    }

    @Test
    @DisplayName("testRegisterParticipant_AlreadyRegisteredOnEvent")
    public void testRegisterParticipant_AlreadyRegisteredOnEvent() {
        when(eventValidator.validateParticipationRegistered(eventId, userId))
                .thenThrow(new EventParticipationRegistrationException("User already registered on event"));
        EventParticipationRegistrationException exception = assertThrows(EventParticipationRegistrationException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(1)).validateUser(userId);
        verify(eventValidator, times(1)).validateParticipationRegistered(eventId, userId);
        verify(eventParticipationRepository, times(0)).register(eventId, userId);
        assertEquals("User already registered on event", exception.getMessage());
    }

    @Test
    @DisplayName("testUnregisterParticipant")
    public void testUnregisterParticipant() {
        eventParticipationService.unregisterParticipant(eventId, userId);
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(1)).validateUser(userId);
        verify(eventValidator, times(1)).validateParticipationNotRegistered(eventId, userId);
        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    @DisplayName("testUnregisterParticipant_EventNotExists")
    public void testUnregisterParticipant_EventNotExists() {
        when(eventValidator.validateEvent(eventId)).thenThrow(new EventExistsException("Event not exists"));
        EventExistsException exception = assertThrows(EventExistsException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(0)).validateUser(userId);
        verify(eventValidator, times(0)).validateParticipationNotRegistered(eventId, userId);
        verify(eventParticipationRepository, times(0)).unregister(eventId, userId);
        assertEquals("Event not exists", exception.getMessage());
    }

    @Test
    @DisplayName("testUnregisterParticipant_UserNotExists")
    public void testUnregisterParticipant_UserNotExists() {
        when(eventValidator.validateUser(userId)).thenThrow(new UserExistsException("User not exists"));
        UserExistsException exception = assertThrows(UserExistsException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(1)).validateUser(userId);
        verify(eventValidator, times(0)).validateParticipationNotRegistered(eventId, userId);
        verify(eventParticipationRepository, times(0)).unregister(eventId, userId);
        assertEquals("User not exists", exception.getMessage());
    }

    @Test
    @DisplayName("testUnregisterParticipant_NotRegisteredOnEvent")
    public void testUnregisterParticipant_NotRegisteredOnEvent() {
        when(eventValidator.validateParticipationNotRegistered(eventId, userId))
                .thenThrow(new EventParticipationRegistrationException("User not registered on event"));
        EventParticipationRegistrationException exception = assertThrows(EventParticipationRegistrationException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
        verify(eventValidator, times(1)).validateEvent(eventId);
        verify(eventValidator, times(1)).validateUser(userId);
        verify(eventValidator, times(1)).validateParticipationNotRegistered(eventId, userId);
        verify(eventParticipationRepository, times(0)).unregister(eventId, userId);
        assertEquals("User not registered on event", exception.getMessage());
    }
}
