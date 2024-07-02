package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.CommonException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.serice.event.EventParticipationService;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    void testRegisterParticipantNegativeCase() {
        User user = new User();
        user.setId(2L);
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assert.assertThrows(CommonException.class, () -> eventParticipationService.registerParticipant(1L, 2L));
    }

    @Test
    void testRegisterParticipantPositiveCase() {
        eventParticipationService.registerParticipant(1L, 3L);
        Mockito.verify(eventParticipationRepository).register(1L, 3L);
    }

    @Test
    void testUnregisterParticipantNegativeCase() {
        User user = new User();
        user.setId(2L);
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of(user));
        Assert.assertThrows(CommonException.class, () -> eventParticipationService.registerParticipant(1L, 2L));
    }

    @Test
    void testUnregisterParticipantPositiveCase() {
        eventParticipationService.unregisterParticipant(1L, 3L);
        Mockito.verify(eventParticipationRepository).unregister(1L, 3L);
    }

    @Test
    void getParticipantTest() {
        eventParticipationService.getParticipant(1L);
        Mockito.verify(eventParticipationRepository).findAllParticipantsByEventId(1L);
    }

    @Test
    void getParticipantsCountTest() {
        eventParticipationService.getParticipantsCount(1L);
        Mockito.verify(eventParticipationRepository).countParticipants(1L);
    }
}
