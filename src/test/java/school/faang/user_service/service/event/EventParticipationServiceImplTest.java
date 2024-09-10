package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceImplTest {
    private static final String EVENT_NOT_EXISTS_EXCEPTION_MESSAGE = "Event not exist";
    private static final String USER_NOT_EXISTS_EXCEPTION_MESSAGE = "User not exist";
    private static final String USER_ALREADY_REGISTERED_EXCEPTION_MESSAGE = "User already registered on event";
    private static final String USER_NOT_REGISTERED_EXCEPTION_MESSAGE = "User not registered on event";
    private final long eventId = 1L;
    private final long userId = 1L;

    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventParticipationValidator eventParticipationValidator;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void testRegisterParticipant_Success() {
        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).register(eventId, userId);
    }

    @Test
    public void testRegisterParticipant_EventNotExists() {
        doThrow(new EntityNotFoundException(EVENT_NOT_EXISTS_EXCEPTION_MESSAGE))
                .when(eventParticipationValidator)
                .checkEventExists(eventId);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
    }

    @Test
    public void testRegisterParticipant_UserNotExists() {
        doThrow(new EntityNotFoundException(USER_NOT_EXISTS_EXCEPTION_MESSAGE))
                .when(eventParticipationValidator)
                .checkUserExists(userId);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
    }

    @Test
    public void testRegisterParticipant_AlreadyRegisteredOnEvent() {
        doThrow(new EventParticipationRegistrationException(USER_ALREADY_REGISTERED_EXCEPTION_MESSAGE))
                .when(eventParticipationValidator)
                .validateParticipationRegistered(eventId, userId);

        assertThrows(EventParticipationRegistrationException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });
    }

    @Test
    public void testUnregisterParticipant_Success() {
        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository, times(1)).unregister(eventId, userId);
    }

    @Test
    public void testUnregisterParticipant_EventNotExists() {
        doThrow(new EntityNotFoundException(EVENT_NOT_EXISTS_EXCEPTION_MESSAGE))
                .when(eventParticipationValidator)
                .checkEventExists(eventId);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
    }

    @Test
    public void testUnregisterParticipant_UserNotExists() {
        doThrow(new EntityNotFoundException(USER_NOT_EXISTS_EXCEPTION_MESSAGE))
                .when(eventParticipationValidator)
                .checkUserExists(userId);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
    }

    @Test
    public void testUnregisterParticipant_NotRegisteredOnEvent() {
        doThrow(new EventParticipationRegistrationException(USER_NOT_REGISTERED_EXCEPTION_MESSAGE))
                .when(eventParticipationValidator)
                .validateParticipationNotRegistered(eventId, userId);

        assertThrows(EventParticipationRegistrationException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });
    }

    @Test
    public void testGetParticipants_Success() {
        List<User> users = createUsers();

        setUpCheckEventExistsResult(false);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);

        List<EventUserDto> result = eventParticipationService.getParticipants(eventId);

        verify(eventParticipationValidator, times(1)).checkEventExists(eventId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(userMapper, times(1)).usersToUserDtos(anyList());

        List<Long> resultIds = result.stream()
                .map(EventUserDto::id)
                .toList();
        List<Long> expectedIds = users.stream()
                .map(User::getId)
                .toList();
        assertEquals(expectedIds, resultIds);
    }

    @Test
    public void testGetParticipants_EmptyParticipantsDto() {
        List<User> users = List.of();
        List<EventUserDto> participantsDto = List.of();

        setUpCheckEventExistsResult(false);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);
        when(userMapper.usersToUserDtos(users)).thenReturn(participantsDto);

        List<EventUserDto> result = eventParticipationService.getParticipants(eventId);

        verify(eventParticipationValidator, times(1)).checkEventExists(eventId);
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(userMapper, times(1)).usersToUserDtos(anyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetParticipants_EventNotExists() {
        setUpCheckEventExistsResult(true);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.getParticipants(eventId);
        });
    }

    @Test
    public void testGetParticipantsCount_Success() {
        List<User> users = createUsers();
        int expectedParticipantsCount = users.size();
        EventParticipantsDto participantsCountDto = new EventParticipantsDto(expectedParticipantsCount);

        setUpCheckEventExistsResult(false);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedParticipantsCount);

        EventParticipantsDto result = eventParticipationService.getParticipantsCount(eventId);

        verify(eventParticipationValidator, times(1)).checkEventExists(eventId);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
        assertEquals(participantsCountDto, result);
    }

    @Test
    public void testGetParticipantsCount_EventNotExists() {
        setUpCheckEventExistsResult(true);

        assertThrows(EntityNotFoundException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });
    }

    @Test
    public void testGetParticipantsCount_ZeroCount() {
        int expectedParticipantsCount = 0;
        EventParticipantsDto participantsCountDto = new EventParticipantsDto(expectedParticipantsCount);

        setUpCheckEventExistsResult(false);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedParticipantsCount);

        EventParticipantsDto result = eventParticipationService.getParticipantsCount(eventId);

        verify(eventParticipationValidator, times(1)).checkEventExists(eventId);
        verify(eventParticipationRepository, times(1)).countParticipants(eventId);
        assertEquals(participantsCountDto, result);
    }

    private void setUpCheckEventExistsResult(boolean generateException) {
        if (generateException) {
            doThrow(new EntityNotFoundException(EVENT_NOT_EXISTS_EXCEPTION_MESSAGE))
                    .when(eventParticipationValidator)
                    .checkEventExists(eventId);
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
}