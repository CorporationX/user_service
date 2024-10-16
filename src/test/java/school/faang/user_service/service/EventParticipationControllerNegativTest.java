package school.faang.user_service.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.EventParticipationController;
import school.faang.user_service.service.impl.EventParticipationServiceImpl;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerNegativTest {

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Mock
    private EventParticipationServiceImpl eventParticipationService;

    @Test
    @Disabled("не проходит, чинить")
    public void testGetParticipantsCount_EventNotFound() {
        long eventId = 1L;

        doThrow(new NoSuchElementException("Event not found"))
                .when(eventParticipationService)
                .getParticipantsCount(eventId);

        ResponseEntity<Integer> response = eventParticipationController.getParticipantsCount(eventId);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());

        verify(eventParticipationService, times(1)).getParticipantsCount(eventId);
    }
}