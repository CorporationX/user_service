package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.commonMessages.ErrorMessagesForEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.RegistrationUserForEventException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.commonMessages.ErrorMessagesForEvent.USER_IS_ALREADY_REGISTERED_FORMAT;
import static school.faang.user_service.commonMessages.ErrorMessagesForEvent.USER_IS_NOT_REGISTERED_FORMAT;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private static final long EXISTING_USER_ID = 1L;

    private long eventId;
    private long someUserId;
    private List<User> users;

    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Spy
    private EventMapper mapper = Mappers.getMapper(EventMapper.class);
    @InjectMocks
    private EventParticipationService eventParticipationService;

    @BeforeEach
    void setUp() {
        eventId = 2L;
        someUserId = 10L;

        users = getUsers();
    }

    @Test
    void testRegisterParticipant_WhenUserNotRegisteredAtEvent() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);

        eventParticipationService.registerParticipant(eventId, someUserId);

        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .register(eventId, someUserId);
    }

    @Test
    void testRegisterParticipant_WhenUserRegisteredAtEvent_ShouldThrowException() {
        String expectedMessage = MessageFormat.format(USER_IS_ALREADY_REGISTERED_FORMAT, EXISTING_USER_ID, eventId);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, EXISTING_USER_ID));

        assertEquals(expectedMessage, exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNullInputData")
    void testRegisterParticipant_WhenInputDataIsNull_ShouldThrowException(
            Long eventId, Long userId, String expectedMessage) {

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

        assertEquals(expectedMessage, exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNegativeInputData")
    void testRegisterParticipant_WhenInputDataIsNegative_ShouldThrowException(
            Long eventId, Long userId, String expectedMessage) {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

        assertEquals(expectedMessage, exc.getMessage());
    }


    @Test
    void testUnregisterParticipant_WhenUserRegisteredAtEvent() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);

        eventParticipationService.unregisterParticipant(eventId, EXISTING_USER_ID);

        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .unregister(eventId, EXISTING_USER_ID);
    }

    @Test
    void testUnregisterParticipant_WhenUserNotRegisteredAtEvent_ShouldThrowException() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);
        String expectedMessage = MessageFormat.format(USER_IS_NOT_REGISTERED_FORMAT, someUserId, eventId);

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, someUserId));

        assertEquals(expectedMessage, exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNullInputData")
    void testUnregisterParticipant_WhenInputDataIsNull_ShouldThrowException(
            Long eventId, Long userId, String expectedMessage) {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));

        assertEquals(expectedMessage, exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNegativeInputData")
    void testUnregisterParticipant_WhenInputDataIsNegative_ShouldThrowException(
            Long eventId, Long userId, String expectedMessage) {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));

        assertEquals(expectedMessage, exc.getMessage());
    }


    @Test
    void testGetParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);
        List<UserDto> expectedUsers = getUsersDto();

        assertEquals(expectedUsers, eventParticipationService.getParticipant(eventId));
    }

    @Test
    void testGetParticipant_WhenEventIdIsNull_shouldThrowException() {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.getParticipant(null));
        String expectedMessage = ErrorMessagesForEvent.EVENT_ID_IS_NULL;

        assertEquals(expectedMessage, exc.getMessage());
    }

    @Test
    void testGetParticipant_WhenEventIdIsNegative_shouldThrowException() {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.getParticipant(-1L));
        String expectedMessage = ErrorMessagesForEvent.NEGATIVE_EVENT_ID;

        assertEquals(expectedMessage, exc.getMessage());
    }


    @Test
    void testGetParticipantsCount() {
        Mockito.when(eventParticipationRepository.countParticipants(eventId))
                .thenReturn(3);

        assertEquals(3, eventParticipationService.getParticipantsCount(eventId));
        Mockito.verify(eventParticipationRepository, Mockito.times(1)).countParticipants(eventId);
    }

    @Test
    void testGetParticipantsCount_WhenEventIdIsNull_shouldThrowException() {
        String expectedMessage = ErrorMessagesForEvent.EVENT_ID_IS_NULL;

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.getParticipantsCount(null));

        assertEquals(expectedMessage, exc.getMessage());
    }

    @Test
    void testGetParticipantsCount_WhenEventIdIsNegative_shouldThrowException() {
        String expectedMessage = ErrorMessagesForEvent.NEGATIVE_EVENT_ID;

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.getParticipantsCount(-1L));

        assertEquals(expectedMessage, exc.getMessage());
    }


    private static Stream<Arguments> provideNullInputData() {
        return Stream.of(
                Arguments.of(null, 1L, ErrorMessagesForEvent.EVENT_ID_IS_NULL),
                Arguments.of(1L, null, ErrorMessagesForEvent.USER_ID_IS_NULL),
                Arguments.of(null, null, ErrorMessagesForEvent.EVENT_ID_IS_NULL)
        );
    }

    private static Stream<Arguments> provideNegativeInputData() {
        return Stream.of(
                Arguments.of(-1L, 1L, ErrorMessagesForEvent.NEGATIVE_EVENT_ID),
                Arguments.of(1L, -1L, ErrorMessagesForEvent.NEGATIVE_USER_ID),
                Arguments.of(-1L, -1L, ErrorMessagesForEvent.NEGATIVE_EVENT_ID)
        );
    }

    private List<User> getUsers() {
        return List.of(
                User.builder().id(EXISTING_USER_ID).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build()
        );
    }

    private List<UserDto> getUsersDto() {
        return List.of(
                UserDto.builder().id(EXISTING_USER_ID).build(),
                UserDto.builder().id(2L).build(),
                UserDto.builder().id(3L).build()
        );
    }
}