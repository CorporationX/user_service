package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.EventParticipationController;
import school.faang.user_service.service.impl.EventParticipationServiceImpl;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerSuccessTest {

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Mock
    private EventParticipationServiceImpl eventParticipationService;

    @Test
    public void testGetParticipantsCountSuccess() {
        long eventId = 1L;
        int expectedCount = 5;

        when(eventParticipationService.getParticipantsCount(eventId)).thenReturn(expectedCount);

        ResponseEntity<Integer> response = eventParticipationController.getParticipantsCount(eventId);
        assertEquals(expectedCount, response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        verify(eventParticipationService, times(1)).getParticipantsCount(eventId);
    }
}