package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;
    @InjectMocks
    private EventParticipationController eventParticipationController;


    @Test
    public void testRegister() {
        long eventId = 1L;
        long userId = 1L;
        eventParticipationController.addParticipant(eventId, userId);
        Mockito.verify(eventParticipationService).addParticipant(eventId, userId);
    }

    @Test
    public void testUnregister() {
        long eventId = 1L;
        long userId = 1L;
        eventParticipationController.removeParticipant(eventId, userId);
        Mockito.verify(eventParticipationService).removeParticipant(eventId, userId);
    }

    @Test
    public void testGetParticipant() {
        long eventId = 1L;
        Mockito.when(eventParticipationService.getParticipant(eventId)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(eventParticipationController.getParticipant(eventId), new ArrayList<>());
    }

    @Test
    public void testGetParticipantsCount() {
        long eventId = 1L;
        int result = 11;
        Mockito.when(eventParticipationService.getParticipantsCount(eventId)).thenReturn(result);
        Assertions.assertEquals(eventParticipationController.getParticipantsCount(eventId), result);
    }
}
