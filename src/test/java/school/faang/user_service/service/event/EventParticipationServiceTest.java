package school.faang.user_service.service.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.EventRegistrationException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private static final long EVENT_ID = 3L;
    private static final long USER_ID = 3L;
    private static final long NEW_USER_ID = 10L;
    private static final int COUNT = 1;

    private static List<User> mockedUsers;

    @Mock
    EventParticipationRepository eventParticipationRepository;

    @InjectMocks
    EventParticipationService eventParticipationService;

    @BeforeAll
    static void setUp() {
        mockedUsers = new ArrayList<>();
        User u1 = new User();
        u1.setId(USER_ID);
        mockedUsers.add(u1);
    }

    @Test
    @DisplayName("should register new user")
    void testRegister() {
        when(eventParticipationRepository.findAllParticipantsByEventAndUserId(EVENT_ID, NEW_USER_ID)).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(EVENT_ID, NEW_USER_ID));
        Mockito.verify(eventParticipationRepository).register(EVENT_ID, NEW_USER_ID);
    }

    @Test
    @DisplayName("shouldn't register existing user")
    void testDuplicateRegistry() {
        when(eventParticipationRepository.findAllParticipantsByEventAndUserId(EVENT_ID, USER_ID)).thenReturn(mockedUsers);
        assertThrows(EventRegistrationException.class, () -> eventParticipationService.registerParticipant(EVENT_ID,
                USER_ID));
    }

    @Test
    @DisplayName("should unregister user")
    void testUnregister() {
        when(eventParticipationRepository.findAllParticipantsByEventAndUserId(EVENT_ID, USER_ID)).thenReturn(mockedUsers);
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(EVENT_ID, USER_ID));
        Mockito.verify(eventParticipationRepository).unregister(EVENT_ID, USER_ID);
    }

    @Test
    @DisplayName("shouldn't unregister unregistered")
    void testUnregisterNonParticipant() {
        when(eventParticipationRepository.findAllParticipantsByEventAndUserId(EVENT_ID, NEW_USER_ID)).thenReturn(Collections.emptyList());
        assertThrows(EventRegistrationException.class, () -> eventParticipationService.unregisterParticipant(EVENT_ID,
                NEW_USER_ID));
    }

    @Test
    @DisplayName("should list all participants")
    void getParticipants() {
        when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID)).thenReturn(mockedUsers);
        assertIterableEquals(mockedUsers, eventParticipationService.getParticipants(EVENT_ID));
    }

    @Test
    @DisplayName("should give participants count")
    void getParticipantsCount() {
        when(eventParticipationRepository.countParticipants(EVENT_ID)).thenReturn(COUNT);
        assertEquals(COUNT, eventParticipationService.getParticipantsCount(EVENT_ID));
    }
}
