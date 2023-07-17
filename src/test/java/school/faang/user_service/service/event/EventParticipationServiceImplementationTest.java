package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.RegistrationUserForEventException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplementationTest {
    private static final long EXISTING_USER_ID = 1L;

    private long eventId;
    private long someUserId;
    private List<User> users;


    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private EventParticipationService eventParticipationService;

    @BeforeEach
    void setUp() {
        eventParticipationService =
                new EventParticipationServiceImplementation(eventParticipationRepository);

        eventId = 1L;
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
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, EXISTING_USER_ID));

        assertEquals("The user has already been registered for the event",
                exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNullInputData")
    void testRegisterParticipant_WhenInputDataIsNull_ShouldThrowException(Long eventId, Long userId) {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

        assertEquals("Input data is null",
                exc.getMessage());
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

        String expectedErrorMessage = String.format("the userId: [%s] is not registered for the eventId: [%s]",
                someUserId, eventId);

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, someUserId));

        assertEquals(expectedErrorMessage,
                exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNullInputData")
    void testUnregisterParticipant_WhenInputDataIsNull_ShouldThrowException(Long eventId, Long userId) {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId));

        assertEquals("Input data is null",
                exc.getMessage());
    }

    @Test
    void testGetParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);
        List<User> expectedUsers = getUsers();

        assertEquals(expectedUsers, eventParticipationService.getParticipant(eventId));
    }

    @Test
    void testGetParticipant_WhenEventIdIsNull_shouldThrowException() {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.getParticipant(null));

        assertEquals("Input data is null",
                exc.getMessage());
    }


    private static Stream<Arguments> provideNullInputData() {
        return Stream.of(
                Arguments.of(null, 1L),
                Arguments.of(1L, null),
                Arguments.of(null, null)
        );
    }

    private List<User> getUsers() {
        return List.of(
                User.builder().id(EXISTING_USER_ID).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build()
        );
    }
}