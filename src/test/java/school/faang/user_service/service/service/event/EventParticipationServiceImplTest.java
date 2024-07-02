package school.faang.user_service.service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.eventServiceImpl.EventParticipationServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceImplTest {

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapper userMapper;

    @Test
    @DisplayName("Test register existing user on event")
    public void testRegisterWithExistingUserAndEvent() {
        when(eventParticipationRepository.checkParticipantAtEvent(anyLong(), anyLong())).thenReturn(1);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(1L, 2L));
    }

    @Test
    @DisplayName("Test register user on event")
    public void testRegisterUserOnEvent() {
        when(eventParticipationRepository.checkParticipantAtEvent(1L, 2L)).thenReturn(0);
        eventParticipationService.registerParticipant(1L, 2L);
        verify(eventParticipationRepository, times(1)).register(1L, 2L);
    }

    @Test
    @DisplayName("Test unregister existing user on event")
    public void testUnregisterExistingUserOnEvent() {
        when(eventParticipationRepository.checkParticipantAtEvent(1L, 2L)).thenReturn(1);
        eventParticipationService.unregisterParticipant(1L, 2L);
        verify(eventParticipationRepository, times(1)).unregister(1L, 2L);
    }

    @Test
    @DisplayName("Test unregister user on event")
    public void testUnregisterUserOnEvent() {
        when(eventParticipationRepository.checkParticipantAtEvent(1L, 2L)).thenReturn(0);
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
