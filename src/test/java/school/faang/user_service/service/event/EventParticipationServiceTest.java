package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;


    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private UserRepository userRepository;

    private Event event;
    private User user;
    private Long userId = 1L;
    private Long eventId = 2L;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(userId);
        event = new Event();
        event.setId(eventId);
    }

    @Test
    public void testRegisterParticipantSuccess() {
        Mockito.when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.verify(eventParticipationRepository, Mockito.atLeastOnce()).register(eventId, userId);
    }

    @Test
    public void testUnregisterSuccess() {
        Mockito.when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.verify(eventParticipationRepository, Mockito.atLeastOnce()).register(eventId, userId);
    }

    @Test
    public void getParticipant() {
        List<User> list = List.of(user);

        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(list);
        List<User> result = eventParticipationService.getParticipant(eventId);

        assertEquals(result, list);
        Mockito.verify(eventParticipationRepository, Mockito.atLeastOnce()).findAllParticipantsByEventId(eventId);
    }

    @Test
    public void getParticipantsCount() {
        List<User> list = List.of(user);

        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(list);
        long result = eventParticipationService
                .getParticipant(eventId)
                .stream()
                .count();

        assertEquals(result, list.size());
        Mockito.verify(eventParticipationRepository, Mockito.atLeastOnce()).findAllParticipantsByEventId(eventId);
    }


    @Test
    public void validationUserNotExist() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Mockito.when(eventParticipationRepository.existsById(eventId)).thenReturn(true);

        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.validateEventAndUserIds(eventId, userId));

        assertEquals("User does not exist", ex.getMessage());
        Mockito.verify(userRepository, Mockito.atLeastOnce()).findById(userId);
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 1, Event does not exist",
            "2, -1, User does not exist",
            "2, 1, User already registered"
    })
    public void validationUserAlreadyRegistered(Long eventId, Long userId, String resultMessage) {
        if (this.eventId == eventId) {
            Mockito.when(eventParticipationRepository.existsById(this.eventId)).thenReturn(true);
            if (this.userId == userId) {
                Mockito.when(userRepository.findById(this.userId)).thenReturn(Optional.ofNullable(user));
                Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(this.eventId)).thenReturn(List.of(user));
            } else {
                Mockito.when(userRepository.findById(this.userId)).thenReturn(Optional.empty());
            }
        } else {
            Mockito.when(eventParticipationRepository.existsById(this.eventId)).thenReturn(false);
        }

        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> eventParticipationService.validateEventAndUserIds(this.eventId, this.userId));

        assertEquals(resultMessage, ex.getMessage());
        Mockito.verify(userRepository, Mockito.atMostOnce()).findById(this.userId);
        Mockito.verify(eventParticipationRepository, Mockito.atMostOnce()).existsById(this.eventId);
        Mockito.verify(eventParticipationRepository, Mockito.atMostOnce()).findAllParticipantsByEventId(this.eventId);
    }
}
