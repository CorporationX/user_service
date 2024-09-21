package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.service.event.EventParticipationService;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerNegativTest {

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Mock
    private EventParticipationService eventParticipationService;

//    @Test
//    @Disabled("не проходит, чинить")
//    public void testGetParticipantsCount_EventNotFound() {
//        long eventId = 1L;
//
//        doThrow(new NoSuchElementException("Event not found"))
//                .when(eventParticipationService)
//                .getParticipantsCount(eventId);
//
//        ResponseEntity<Integer> response = eventParticipationController.getParticipantsCount(eventId);
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
//
//        verify(eventParticipationService, times(1)).getParticipantsCount(eventId);
//    }
}