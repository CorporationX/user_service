package school.faang.user_service.service.eventService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventParticipantValidator;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventParticipantValidator eventParticipantValidator;

    @Spy
    private UserMapper userMapper;

    @Test
    @DisplayName("Test register user on event")
    public void testRegisterUserAndEvent() {
        eventParticipationService.registerParticipant(1L, 2L);
        verify(eventParticipantValidator, times(1)).checkNoRegistrationAtEvent(1L, 2L);
        verify(eventParticipationRepository, times(1)).register(1L, 2L);
    }

    @Test
    @DisplayName("Test unregister user on event")
    public void testUnregisterUserOnEvent() {
        eventParticipationService.unregisterParticipant(1L, 2L);
        verify(eventParticipantValidator, times(1)).checkRegistrationAtEvent(1L, 2L);
        verify(eventParticipationRepository, times(1)).unregister(1L, 2L);
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
