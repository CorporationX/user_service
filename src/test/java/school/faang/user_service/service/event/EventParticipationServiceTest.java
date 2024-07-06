package school.faang.user_service.service.event;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterExistingUser() {
        long eventId = 0L;
        long userId = 0L;
        prepareUserList(eventId, userId);
        Assert.assertThrows(RuntimeException.class, () -> eventParticipationService.registerParticipant(eventId, userId));
    }

    @Test
    public void testRegister() {
        long eventId = 0L;
        long userId = 0L;
        prepareUserList(eventId, 1L);
        eventParticipationService.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testUnregisterNotExistingUser() {
        long eventId = 0L;
        long userId = 0L;
        prepareUserList(eventId, 1L);
        Assert.assertThrows(RuntimeException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    public void testUnregister() {
        long eventId = 0L;
        long userId = 0L;
        prepareUserList(eventId, userId);
        eventParticipationService.unregisterParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    public void testGetParticipant() {
        long eventId = 0L;
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(eventParticipationService.getParticipant(eventId), new ArrayList<>());
    }

    @Test
    public void testGetParticipantsCount() {
        long eventId = 0L;
        int result = 11;
        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(result);
        Assertions.assertEquals(eventParticipationService.getParticipantsCount(eventId), result);
    }


    private void prepareUserList(long eventId, long userId) {
        User user = new User();
        user.setId(userId);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
    }
}
