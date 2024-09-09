package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventParticipantsDto;
import school.faang.user_service.dto.EventUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.EventParticipationRegistrationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventParticipationServiceTest")
public class EventParticipationServiceImplTest {
    private final long eventId = 1L;
    private final long userId = 1L;
    private static final String EVENT_NOT_EXISTS_EXCEPTION_MESSAGE = "Event not exist";
    private static final String USER_NOT_EXISTS_EXCEPTION_MESSAGE = "User not exist";
    private static final String USER_ALREADY_REGISTERED_EXCEPTION_MESSAGE = "User already registered on event";
    private static final String USER_NOT_REGISTERED_EXCEPTION_MESSAGE = "User not registered on event";

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventParticipationValidator eventParticipationValidator;

    @Spy
    private UserMapper userMapper;

    @Test
    @DisplayName("testRegisterParticipant_Success")
    public void testRegisterParticipant_Success() {
        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    @DisplayName("testRegisterParticipant_EventNotExists")
    public void testRegisterParticipant_EventNotExists() {
        when(eventParticipationValidator.checkEventExists(eventId)).thenThrow(new EntityNotFoundException(EVENT_NOT_EXISTS_EXCEPTION_MESSAGE));

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
    }

    @Test
    @DisplayName("testRegisterParticipant_UserNotExists")
    public void testRegisterParticipant_UserNotExists() {
        when(eventParticipationValidator.checkUserExists(userId)).thenThrow(new EntityNotFoundException(USER_NOT_EXISTS_EXCEPTION_MESSAGE));

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
    }

    @Test
    @DisplayName("testRegisterParticipant_AlreadyRegisteredOnEvent")
    public void testRegisterParticipant_AlreadyRegisteredOnEvent() {
        when(eventParticipationValidator.validateParticipationRegistered(eventId, userId))
                .thenThrow(new EventParticipationRegistrationException(USER_ALREADY_REGISTERED_EXCEPTION_MESSAGE));

        assertThrows(EventParticipationRegistrationException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
    }

    @Test
    @DisplayName("testUnregisterParticipant_Success")
    public void testUnregisterParticipant_Success() {
        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    @DisplayName("testUnregisterParticipant_EventNotExists")
    public void testUnregisterParticipant_EventNotExists() {
        when(eventParticipationValidator.checkEventExists(eventId)).thenThrow(new EntityNotFoundException(EVENT_NOT_EXISTS_EXCEPTION_MESSAGE));

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
    }

    @Test
    @DisplayName("testUnregisterParticipant_UserNotExists")
    public void testUnregisterParticipant_UserNotExists() {
        when(eventParticipationValidator.checkUserExists(userId)).thenThrow(new EntityNotFoundException(USER_NOT_EXISTS_EXCEPTION_MESSAGE));

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
    }

    @Test
    @DisplayName("testUnregisterParticipant_NotRegisteredOnEvent")
    public void testUnregisterParticipant_NotRegisteredOnEvent() {
        when(eventParticipationValidator.validateParticipationNotRegistered(eventId, userId))
                .thenThrow(new EventParticipationRegistrationException(USER_NOT_REGISTERED_EXCEPTION_MESSAGE));

        assertThrows(EventParticipationRegistrationException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
    }

    @Test
    @DisplayName("testGetParticipants_Success")
    public void testGetParticipants_Success() {
        List<User> users = createUsers();
        List<EventUserDto> participantsDto = createParticipantsDto();

        setUpEventValidator(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);
        when(userMapper.usersToUsersDto(users)).thenReturn(participantsDto);

        List<EventUserDto> result = eventParticipationService.getParticipants(eventId);

        verifyGetParticipants();
        assertEquals(participantsDto, result);
    }

    @Test
    @DisplayName("testGetParticipants_EmptyParticipantsDto")
    public void testGetParticipants_EmptyParticipantsDto() {
        List<User> users = List.of();
        List<EventUserDto> participantsDto = List.of();

        setUpEventValidator(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);
        when(userMapper.usersToUsersDto(users)).thenReturn(participantsDto);

        List<EventUserDto> result = eventParticipationService.getParticipants(eventId);

        verifyGetParticipants();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("testGetParticipants_EventNotExists")
    public void testGetParticipants_EventNotExists() {
        setUpEventValidator(false);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.getParticipants(eventId);
        });
    }

    @Test
    @DisplayName("testGetParticipantsCount_Success")
    public void testGetParticipantsCount_Success() {
        List<User> users = createUsers();
        int expectedParticipantsCount = users.size();
        EventParticipantsDto participantsCountDto = new EventParticipantsDto(expectedParticipantsCount);

        setUpEventValidator(true);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedParticipantsCount);

        EventParticipantsDto result = eventParticipationService.getParticipantsCount(eventId);

        verifyGetParticipantsCount();
        assertEquals(participantsCountDto, result);
    }

    @Test
    @DisplayName("testGetParticipantsCount_EventNotExists")
    public void testGetParticipantsCount_EventNotExists() {
        setUpEventValidator(false);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });
    }

    @Test
    @DisplayName("testGetParticipantsCount_ZeroCount")
    public void testGetParticipantsCount_ZeroCount() {
        int expectedParticipantsCount = 0;
        EventParticipantsDto participantsCountDto = new EventParticipantsDto(expectedParticipantsCount);

        setUpEventValidator(true);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedParticipantsCount);

        EventParticipantsDto result = eventParticipationService.getParticipantsCount(eventId);

        verifyGetParticipantsCount();
        assertEquals(participantsCountDto, result);
    }

    private void setUpEventValidator(boolean isValid) {
        if (isValid) {
            when(eventParticipationValidator.checkEventExists(eventId)).thenReturn(true);
        } else {
            when(eventParticipationValidator.checkEventExists(eventId)).thenThrow(new EntityNotFoundException("Event not exists"));
        }
    }

    private List<User> createUsers() {
        User firstUser = User.builder()
                .id(1L)
                .username("Tom")
                .email("tom@gmail.com").build();
        User secondUser = User.builder()
                .id(2L)
                .username("Bob")
                .email("bob@gmail.com").build();
        return List.of(firstUser, secondUser);
    }

    private List<EventUserDto> createParticipantsDto() {
        return List.of(
                new EventUserDto(1L, "Tom", "tom@gmail.com"),
                new EventUserDto(2L, "Bob", "bob@gmail.com")
        );
    }

    private void verifyGetParticipants() {
        verify(eventParticipationValidator, times(1)).checkEventExists(eventId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(userMapper, times(1)).usersToUsersDto(anyList());
    }

    private void verifyGetParticipantsCount() {
        verify(eventParticipationValidator, times(1)).checkEventExists(eventId);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
    }
}
