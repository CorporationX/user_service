package school.faang.user_service.controller.event;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validation.EventParticipationRequestValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {

    @InjectMocks
    private EventParticipationController controller;
    @Mock
    private EventParticipationService service;
    @Mock
    private EventParticipationRequestValidator validator;


    @Test
    void getParticipantsCountSuccessfully() {
        Long eventId = 123L;
        int count = 5;
        when(service.getParticipantsCount(eventId)).thenReturn(count);

        ResponseEntity<Integer> response = controller.getParticipantsCount(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(count, response.getBody());
        verify(validator).validate(eventId);
        verify(service).getParticipantsCount(eventId);
    }

}