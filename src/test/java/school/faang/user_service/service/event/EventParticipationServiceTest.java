package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotRegisteredAtEvent;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(value = {MockitoExtension.class})
class EventParticipationServiceTest {
    @Mock
    private EventParticipationRepository repository;

    private EventParticipationService service;

    @BeforeEach
    void setUp(){
        service = new EventParticipationService(repository);
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
}