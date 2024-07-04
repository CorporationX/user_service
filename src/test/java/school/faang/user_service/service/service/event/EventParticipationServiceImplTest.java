package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.Validate.ParticipantOnEventValidator;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.eventService.EventParticipationServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceImplTest {

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private ParticipantOnEventValidator participantOnEventValidator;

    @Spy
    private UserMapper userMapper;

    @Test
    @DisplayName("Test register existing user on event")
    public void testRegisterWithExistingUserAndEvent() {
        when(participantOnEventValidator.checkParticipantAtEvent(anyLong(), anyLong())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(1L, 2L));
    }

    @Test
    @DisplayName("Test register user on event")
    public void testRegisterUserOnEvent() {
        when(participantOnEventValidator.checkParticipantAtEvent(1L, 2L)).thenReturn(false);
        eventParticipationService.registerParticipant(1L, 2L);
        verify(eventParticipationRepository, times(1)).register(1L, 2L);
    }

    @Test
    @DisplayName("Test unregister existing user on event")
    public void testUnregisterExistingUserOnEvent() {
        when(participantOnEventValidator.checkParticipantAtEvent(1L, 2L)).thenReturn(true);
        eventParticipationService.unregisterParticipant(1L, 2L);
        verify(eventParticipationRepository, times(1)).unregister(1L, 2L);
    }

    @Test
    @DisplayName("Test unregister user on event")
    public void testUnregisterUserOnEvent() {
        when(participantOnEventValidator.checkParticipantAtEvent(1L, 2L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(1L, 2L));
    }

    @Test
    @DisplayName("Test get all participants on event")
    public void testGetParticipantsOnEvent() {
        eventParticipationService.getParticipant(anyLong());
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(anyLong());
    }

    @Test
    @DisplayName("Test count participants on event")
    public void testCountParticipantsOnEvent() {
        eventParticipationService.getParticipantsCount(anyLong());
        verify(eventParticipationRepository, times(1)).countParticipants(anyLong());
    }
}
