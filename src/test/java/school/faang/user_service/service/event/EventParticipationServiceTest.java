package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapper userMapper;

    private final long eventId = 1L;
    private final long userId = 1L;


    @Test
    void testFindParticipantsAmountByEventIdThrowExceptionIfEventDoesNotExit() {
        assertThrows(EntityNotFoundException.class, () -> eventParticipationService.findParticipantsAmountByEventId(eventId),
                "Event with id " + eventId + " does not exist");
    }

    @Test
    void testFindParticipantsAmountByEventIdReturnNumberOfParticipants() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(new User(), new User(), new User()));

        var result = eventParticipationService.findParticipantsAmountByEventId(eventId);

        assertEquals(3, result);
    }

    @Test
    void testFindAllParticipantsByEventIdThrowExceptionIfEventDoesNotExit() {
        when(eventService.checkEventExistence(eventId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventParticipationService.findAllParticipantsByEventId(eventId),
                "Event with id " + eventId + " does not exist");
    }

    @Test
    void testFindAllParticipantsByEventIdReturnListOfParticipants() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(
                setUpUser(), new User(), new User()
        ));
        when(userMapper.toDto(any(User.class))).thenReturn(new UserDto());

        var result = eventParticipationService.findAllParticipantsByEventId(eventId);

        assertEquals(3, result.size());
        verify(userMapper, times(3)).toDto(any(User.class));
    }

    @Test
    void testRegisterParticipantThrowExceptionIfEventDoesNotExit() {
        when(eventService.checkEventExistence(eventId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventParticipationService.registerParticipant(eventId, userId),
                "Event with id " + eventId + " does not exist");
    }

    @Test
    void testRegisterParticipantThrowExceptionIfUserDoesNotExit() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(userService.checkUserExistence(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventParticipationService.registerParticipant(eventId, userId),
                "User with id " + userId + " does not exist");
    }

    @Test
    void testRegisterParticipantThrowExceptionIfUserAlreadyRegistered() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(userService.checkUserExistence(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(setUpUser()));

        assertThrows(IllegalStateException.class, () -> eventParticipationService.registerParticipant(eventId, userId),
                "User is already registered for the event");

    }

    @Test
    void testRegisterParticipantRegisterUserSuccessfully() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(userService.checkUserExistence(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventService, times(1)).checkEventExistence(eventId);
        verify(userService, times(1)).checkUserExistence(userId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    void testUnregisterParticipantThrowExceptionIfEventDoesNotExit() {
        when(eventService.checkEventExistence(eventId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId),
                "Event with id " + eventId + " does not exist");
    }

    @Test
    void testUnregisterParticipantThrowExceptionIfUserDoesNotExit() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(userService.checkUserExistence(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId),
                "User with id " + userId + " does not exist");
    }

    @Test
    void testUnregisterParticipantThrowExceptionIfUserNotRegistered() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(userService.checkUserExistence(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> eventParticipationService.unregisterParticipant(eventId, userId),
                "User is not registered for the event");
    }

    @Test
    void testUnregisterParticipantUnregisterUserSuccessfully() {
        when(eventService.checkEventExistence(eventId)).thenReturn(true);
        when(userService.checkUserExistence(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(setUpUser()));

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventService, times(1)).checkEventExistence(eventId);
        verify(userService, times(1)).checkUserExistence(userId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    private User setUpUser() {
        var user = new User();
        user.setId(userId);
        return user;
    }
}