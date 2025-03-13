package school.faang.user_service.controller.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    private static final Long EVENT_ID = 2L;
    private static final Long USER_ID = 1L;

    @Mock
    private EventParticipationService eventParticipationService;
    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    @DisplayName("Register participant for event")
    public void testRegisterParticipant() {
        String expectedMessage = "User with id 1 registered for event with id 2";

        doNothing().when(eventParticipationService).registerParticipant(EVENT_ID, USER_ID);
        ResponseEntity<String> responseEntity = eventParticipationController.registerParticipant(EVENT_ID, USER_ID);

        verify(eventParticipationService, times(1))
                .registerParticipant(anyLong(), anyLong());
        assertEquals(expectedMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Unregister participant for event")
    public void testUnregisterParticipant() {
        String expectedMessage = "User with id 1 unregistered for event with id 2";

        doNothing().when(eventParticipationService).unregisterParticipant(EVENT_ID, USER_ID);
        ResponseEntity<String> responseEntity = eventParticipationController.unregisterParticipant(EVENT_ID, USER_ID);

        verify(eventParticipationService, times(1))
                .unregisterParticipant(EVENT_ID, USER_ID);
        assertEquals(expectedMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Get all participants for event")
    public void testGetParticipant() {
        UserDto alexDto = UserDto.builder()
                .id(USER_ID)
                .username("Alex")
                .email("alex@mail.ru")
                .build();
        List<UserDto> userDtos = List.of(alexDto);

        when(eventParticipationService.getParticipant(EVENT_ID)).thenReturn(userDtos);
        ResponseEntity<List<UserDto>> responseEntity = eventParticipationController.getParticipant(EVENT_ID);

        verify(eventParticipationService, times(1)).getParticipant(EVENT_ID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userDtos, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get empty list of participants for event")
    public void testGetParticipantEmpty() {
        List<UserDto> emptyList = Collections.emptyList();

        when(eventParticipationService.getParticipant(EVENT_ID)).thenReturn(emptyList);
        ResponseEntity<List<UserDto>> responseEntity = eventParticipationController.getParticipant(EVENT_ID);

        verify(eventParticipationService, times(1)).getParticipant(EVENT_ID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(emptyList, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get count participants for event")
    public void testGetParticipantCount() {
        int expectedCount = 1;

        when(eventParticipationService.getParticipantCount(EVENT_ID)).thenReturn(expectedCount);
        ResponseEntity<Integer> responseEntity = eventParticipationController.getParticipantsCount(EVENT_ID);

        verify(eventParticipationService, times(1)).getParticipantCount(EVENT_ID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedCount, responseEntity.getBody());
    }
}
