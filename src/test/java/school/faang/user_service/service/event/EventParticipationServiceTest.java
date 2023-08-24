package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.exception.UserNotRegisteredAtEvent;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(value = {MockitoExtension.class})
class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository repository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    private EventParticipationService service;

    @BeforeEach
    void setUp(){
        service = new EventParticipationService(repository, userMapper);
    }

    @Test
    void registerParticipant_WhereUserNotRegisteredAtEvent() {
        long someUserId = new Random().nextLong();
        long someEventId = new Random().nextLong();

        User existingUser1 = User.builder()
                .id(someUserId + 1)
                .build();
        User existingUser2 = User.builder()
                .id(someUserId - 1)
                .build();
        List<User> registeredUsers = List.of(existingUser1, existingUser2);

        Mockito.when(repository.findAllParticipantsByEventId(someEventId)).thenReturn(registeredUsers);

        assertDoesNotThrow(() -> service.registerParticipant(someUserId, someEventId));
        Mockito.verify(repository).register(someEventId, someUserId);
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
                () -> service.registerParticipant(userId, eventId)
        );

        assertEquals("User with id 777 already registered at event with id 123", e.getMessage());
    }

    @Test
    public void unregisterParticipant_ShouldDeleteUserFromEvent(){
        long someUserId = new Random().nextLong();
        long someEventId = new Random().nextLong();

        User existingUser1 = User.builder()
                .id(someUserId + 1)
                .build();
        User existingUser2 = User.builder()
                .id(someUserId - 1)
                .build();

        List<User> eventParticipants = List.of(existingUser1,existingUser2);

        Mockito.when(repository.findAllParticipantsByEventId(someEventId)).thenReturn(eventParticipants);

        assertDoesNotThrow(() -> service.unregisterParticipant(someEventId, existingUser1.getId()));
        Mockito.verify(repository, Mockito.times(1)).unregister(someEventId, existingUser1.getId());
    }

    @Test
    public void unregisterParticipant_WhenUserNotEvent(){
        long userId = 666L;
        long eventId = 123L;

        User existingUser = User.builder()
                .id(userId)
                .build();

        UserNotRegisteredAtEvent e = assertThrows(
                UserNotRegisteredAtEvent.class,
                () -> service.unregisterParticipant(userId, eventId)
        );
    }
    @Test
    public void getParticipant_ShouldReturnCorrectList() {
        long someUserId = new Random().nextLong();
        long someEventId = new Random().nextLong();

        User existingUser1 = User.builder()
                .id(someUserId + 1)
                .build();
        User existingUser2 = User.builder()
                .id(someUserId - 1)
                .build();

        var eventParticipants = List.of(existingUser1, existingUser2);

        Mockito.when(repository.findAllParticipantsByEventId(someEventId)).thenReturn(eventParticipants);

        assertEquals(2, service.getParticipants(someEventId).size());
    }
    @Test
    public void getParticipantCount_ShouldReturnCorrectParticipantsCount() {
        long someUserId = new Random().nextLong();
        long someEventId = new Random().nextLong();

        User existingUser1 = User.builder()
                .id(someUserId + 1)
                .build();
        User existingUser2 = User.builder()
                .id(someUserId - 1)
                .build();

        var eventParticipants = List.of(existingUser1, existingUser2);

        Mockito.when(repository.countParticipants(someEventId)).thenReturn(eventParticipants.size());

        assertEquals(2, service.getParticipantsCount(someEventId));
    }
}