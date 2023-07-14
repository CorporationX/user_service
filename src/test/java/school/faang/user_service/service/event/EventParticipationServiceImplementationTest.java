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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplementationTest {
    private User user;
    private Long eventId;
    private Long userId;
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private EventParticipationService eventParticipationService;

    @BeforeEach
    void setUp() {
        eventParticipationService =
                new EventParticipationServiceImplementation(eventParticipationRepository);
        user = new User();
        eventId = 1L;
        userId = 1L;
    }

    @Test
    void testRegisterParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(new ArrayList<>(0));

        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .register(eventId, userId);
    }

    @Test
    void testUnregisterParticipant() {
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(user));

        eventParticipationService.unregisterParticipant(eventId, userId);

        Mockito.verify(eventParticipationRepository, Mockito.times(1))
                .unregister(eventId, userId);
    }

    @Test
    @Description("Testing the Register Participation method, the method should throw an exception" +
            " if the user is already participating in the event")
    void shouldThrowExceptionMethodRegisterParticipantWhenTheUserIsRegistered() {
        userId = user.getId();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(List.of(new User(userId)));

        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

        assertEquals("The user has already been registered for the event",
                exc.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideNullInputData")
    void shouldThrowExceptionWhenInputDataIsNull(Long eventId, Long userId) {
        Exception exc = assertThrows(RegistrationUserForEventException.class,
                () -> eventParticipationService.registerParticipant(eventId, userId));

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
}