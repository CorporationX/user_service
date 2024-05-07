package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class EventParticipationControllerTest {

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    public void testRegisterParticipant() {
        long eventId = 1;
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        eventParticipationController.registerParticipant(eventId, userDto);

        verify(eventParticipationService).registerParticipant(eventId, userDto.getId());
    }

    @Test
    void testGetParticipants() {
        MockitoAnnotations.initMocks(this);

        long eventId = 1L;
        User user1 = new User("User1");
        User user2 = new User("User2");
        List<User> expectedParticipants = Arrays.asList(user1, user2);

        when(eventParticipationService.getAllParticipantsByEventId(eventId)).thenReturn(expectedParticipants);
        List<User> actualParticipants = eventParticipationController.getParticipants(eventId);

        Assertions.assertEquals(expectedParticipants, actualParticipants);
    }
}
@SpringBootTest
class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    public void testRegisterParticipant_SuccessfulRegistration() {long eventId = 1;
        long userId = 1;
        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    public void testRegisterParticipant_UserAlreadyRegistered() {
        long eventId = 1;
        long userId = 1;
        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> eventParticipationService.registerParticipant(eventId, userId));
    }
    @Test
    public void testUnregisterParticipant_UserNotRegistered() {
        when(eventParticipationRepository.existsByEventIdAndUserId(anyLong(), anyLong())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(1L, 1L));
        verify(eventParticipationRepository, never()).unregister(anyLong(), anyLong());
    }

    @Test
    public void testUnregisterParticipant_UserRegistered() {
        when(eventParticipationRepository.existsByEventIdAndUserId(anyLong(), anyLong())).thenReturn(true);
        eventParticipationService.unregisterParticipant(1L, 1L);
        verify(eventParticipationRepository, times(1)).unregister(anyLong(), anyLong());
    }

}
@SpringBootTest
class EventParticipationRepositoryTest {

    @Test
    public void testExistsByEventIdAndUserId() {
        EventParticipationRepository eventParticipationRepository = mock(EventParticipationRepository.class);
        long eventId = 1;
        long userId = 1;

        eventParticipationRepository.existsByEventIdAndUserId(eventId, userId);
        verify(eventParticipationRepository).existsByEventIdAndUserId(eventId, userId);
    }
}

