package school.faang.user_service.service.event;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @Mock
    EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    EventParticipationService eventParticipationService;
    static final long EVENT_ID = 3L;
    static final long USER_ID = 3L;
    static final long NEW_USER_ID = 10L;
    static final int COUNT = 1;

    @Test
    @DisplayName("should register new user")
    void testRegister() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(mockedUsers());
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(EVENT_ID, NEW_USER_ID));
    }

    @Test
    @DisplayName("shouldn't register existing user")
    void testDuplicateRegistry() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(mockedUsers());
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(EVENT_ID,
                USER_ID));
    }

    @Test
    @DisplayName("should unregister user")
    void testUnregister() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(mockedUsers());
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));
    }

    @Test
    @DisplayName("shouldn't unregister unregistered")
    void testUnregisterNonParticipant() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(mockedUsers());
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(EVENT_ID,
                NEW_USER_ID));
    }

    @Test
    @DisplayName("should list all participants")
    void getParticipants() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(mockedUsers());
        assertIterableEquals(mockedUsers(), eventParticipationService.getParticipants(EVENT_ID));
    }

    @Test
    @DisplayName("should give participants count")
    void getParticipantsCount() {
        when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(COUNT);
        assertEquals(COUNT, eventParticipationService.getParticipantsCount(EVENT_ID));
    }

    List<User> mockedUsers() {
        List<User> users = new ArrayList<>();
        User u1 = new User();
        u1.setId(USER_ID);
        users.add(u1);
        return users;
    }
}
