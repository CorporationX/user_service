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

    @Mock
    private EventParticipationService eventParticipationService;
    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    @DisplayName("Register participant for event")
    public void testRegisterParticipant() {
        Long eventId = 1L;
        Long userId = 2L;
        String expectedMessage = "User with id 2 registered for event with id 1";

        doNothing().when(eventParticipationService).registerParticipant(eventId, userId);
        ResponseEntity<String> responseEntity = eventParticipationController.registerParticipant(eventId, userId);

        verify(eventParticipationService, times(1))
                .registerParticipant(anyLong(), anyLong());
        assertEquals(expectedMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Unregister participant for event")
    public void testUnregisterParticipant() {
        Long eventId = 1L;
        Long userId = 2L;
        String expectedMessage = "User with id 2 unregistered for event with id 1";

        doNothing().when(eventParticipationService).unregisterParticipant(eventId, userId);
        ResponseEntity<String> responseEntity = eventParticipationController.unregisterParticipant(eventId, userId);

        verify(eventParticipationService, times(1))
                .unregisterParticipant(eventId, userId);
        assertEquals(expectedMessage, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Get all participants for event")
    public void testGetParticipant() {
        Long eventId = 2L;
        UserDto alexDto = UserDto.builder()
                .id(1L)
                .username("Alex")
                .email("alex@mail.ru")
                .build();
        List<UserDto> userDtos = List.of(alexDto);

        when(eventParticipationService.getParticipant(eventId)).thenReturn(userDtos);
        ResponseEntity<List<UserDto>> responseEntity = eventParticipationController.getParticipant(eventId);

        verify(eventParticipationService, times(1)).getParticipant(eventId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userDtos, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get empty list of participants for event")
    public void testGetParticipantEmpty() {
        Long eventId = 2L;
        List<UserDto> emptyList = Collections.emptyList();

        when(eventParticipationService.getParticipant(eventId)).thenReturn(emptyList);
        ResponseEntity<List<UserDto>> responseEntity = eventParticipationController.getParticipant(eventId);

        verify(eventParticipationService, times(1)).getParticipant(eventId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(emptyList, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get count participants for event")
    public void testGetParticipantCount() {
        Long eventId = 1L;
        int expectedCount = 1;

        when(eventParticipationService.getParticipantCount(eventId)).thenReturn(expectedCount);
        ResponseEntity<Integer> responseEntity = eventParticipationController.getParticipantsCount(eventId);

        verify(eventParticipationService, times(1)).getParticipantCount(eventId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedCount, responseEntity.getBody());
    }
}
