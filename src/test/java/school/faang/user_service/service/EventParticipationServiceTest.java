package school.faang.user_service.service;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(value = {MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository repository;

    private EventParticipationService service;

    @BeforeEach
    void setUp() {
        service = new EventParticipationService(repository);
    }

    @Test
    void registerParticipant_WhereUserNotRegisteredAtEvent() {
        long someUserId = 777L;
        long someEventId = 1234L;

        User existingUser1 = User.builder()
                .id(someUserId + 1)
                .build();
        User existingUser2 = User.builder()
                .id(someUserId - 1)
                .build();
        var registeredUsers = List.of(existingUser1, existingUser2);

        Mockito.when(repository.findAllParticipantsByEventId(someEventId)).thenReturn(registeredUsers);

        assertDoesNotThrow(() -> service.registerParticipant(someEventId, someUserId));
        Mockito.verify(repository).register(1234L, 777L);
    }

    @Test
    void registerParticipant_WhereUserAlreadyRegisteredAtEvent() {
        long userId = 777L;
        long eventId = 123L;

        User existingUser = User.builder()
                .id(userId)
                .build();
        var registeredUsers = List.of(existingUser);

        Mockito.when(repository.findAllParticipantsByEventId(eventId)).thenReturn(registeredUsers);

        UserAlreadyRegisteredAtEvent e = assertThrows(
                UserAlreadyRegisteredAtEvent.class,
                () -> service.registerParticipant(eventId, userId)
        );

        assertEquals("User with id 777 already registered at event with id 123", e.getMessage());
    }
}