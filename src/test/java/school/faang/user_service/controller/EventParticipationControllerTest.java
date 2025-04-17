package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {

    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 2L;

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    private UserDto createUserDto(long id) {
        return UserDto.builder()
                .id(id)
                .userName("user" + id)
                .email("user" + id + "@example.com")
                .build();
    }

    @Test
    void registerParticipantShouldCallService() {
        eventParticipationController.registerParticipant(EVENT_ID, USER_ID);
        verify(eventParticipationService).registerParticipant(EVENT_ID, USER_ID);
    }

    @Test
    void unregisterParticipantShouldCallService() {
        eventParticipationController.unregisterParticipant(EVENT_ID, USER_ID);
        verify(eventParticipationService).unregisterParticipant(EVENT_ID, USER_ID);
    }

    @Test
    void getParticipantsShouldCallService() {
        UserDto user1 = createUserDto(1L);
        UserDto user2 = createUserDto(2L);
        List<UserDto> expected = List.of(user1, user2);

        when(eventParticipationService.getParticipants(EVENT_ID)).thenReturn(expected);

        eventParticipationController.getParticipants(EVENT_ID);
        verify(eventParticipationService).getParticipants(EVENT_ID);
    }

    @Test
    void getParticipantsCountShouldCallService() {
        when(eventParticipationService.getParticipantsCount(EVENT_ID)).thenReturn(5);

        eventParticipationController.getParticipantsCount(EVENT_ID);
        verify(eventParticipationService).getParticipantsCount(EVENT_ID);
    }
}