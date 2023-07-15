package school.faang.user_service.controller.event;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validation.EventParticipationRequestValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {

    private static final Long EVENT_ID = 123L;
    private static final Long USER_ID = 333L;

    @InjectMocks
    private EventParticipationController controller;
    @Mock
    private EventParticipationService service;
    @Mock
    private EventParticipationRequestValidator validator;


    @Test
    void registerParticipantSuccessfully() {
        ResponseEntity<Void> response = controller.registerParticipant(EVENT_ID, USER_ID);

        verify(validator).validate(EVENT_ID, USER_ID);
        verify(service).registerParticipant(EVENT_ID, USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getParticipantsCountSuccessfully() {
        int count = 5;
        when(service.getParticipantsCount(EVENT_ID)).thenReturn(count);

        ResponseEntity<Integer> response = controller.getParticipantsCount(EVENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(count, response.getBody());
        verify(validator).validate(EVENT_ID);
        verify(service).getParticipantsCount(EVENT_ID);
    }

    @Test
    void getAllParticipantsSuccessfully() {
        List<UserDto> userDtoList = List.of(
            UserDto.builder().id(EVENT_ID).build(),
            UserDto.builder().id(3L).build(),
            UserDto.builder().id(1L).build()
        );

        when(service.getAllParticipants(EVENT_ID)).thenReturn(userDtoList);

        ResponseEntity<List<UserDto>> response = controller.getAllParticipants(EVENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDtoList, response.getBody());
        verify(validator).validate(EVENT_ID);
        verify(service).getAllParticipants(EVENT_ID);
    }
}