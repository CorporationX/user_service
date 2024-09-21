package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {
    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Mock
    private EventParticipationService eventParticipationService;

    private Long userId = 1L;
    private Long eventId = 2L;

    @Test
    public void registerParticipant() {
        eventParticipationController.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationService, Mockito.atLeastOnce()).registerParticipant(eventId, userId);
    }

    @Test
    public void unregisterParticipant() {
        eventParticipationController.unregisterParticipant(eventId, userId);
        Mockito.verify(eventParticipationService, Mockito.atLeastOnce()).unregisterParticipant(eventId, userId);
    }

    @Test
    public void getParticipant() {
        User user = User.builder()
                .id(userId)
                .build();

       Mockito.when(eventParticipationService.getParticipant(eventId)).thenReturn(List.of(user));
       List<Long> resultList = eventParticipationController.getParticipant(eventId);

        assertEquals(resultList, List.of(userId));
        Mockito.verify(eventParticipationService, Mockito.atLeastOnce()).getParticipant(eventId);
    }
}
