package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.util.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private EventParticipationService eventParticipationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        eventParticipationService = new EventParticipationService(eventParticipationRepository);
    }

    @Test
    void testRegisterParticipant_SuccessfulRegistration() {
        long eventId = 1;
        long userId = 1;
        when(eventParticipationRepository.findById(userId)).thenReturn(Optional.empty());
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    void testRegisterParticipant_DuplicateUserRegistration() {
        long eventId = 1;
        long userId = 1;
        when(eventParticipationRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, userId));
        verify(eventParticipationRepository, never()).register(eventId, userId);
    }

        @Test
        void testUnregisterParticipant_UserRegistered_SuccessfullyUnregistered() {
            long eventId = 1;
            long userId = 123;
            List<User> userList = new ArrayList<>();
            User user = User.builder()
                    .id(userId)
                    .build();
            userList.add(user);
            eventParticipationService.unregisterParticipant(eventId, userId);
            assertFalse(UserUtils.findUserById(userList, userId));
        }

        @Test
        void testUnregisterParticipant_UserNotRegistered_ThrowsException() {
            long eventId = 1;
            long userId = 123;
            List<User> eventUsers = new ArrayList<>();
            User user = User.builder()
                    .id(456)
                    .build();
            eventUsers.add(user);
            assertThrows(IllegalArgumentException.class, () -> {
                eventParticipationService.unregisterParticipant(eventId, userId);
            });
        }

    @Test
    void testGetParticipant_ExistingEventId_ReturnsListOfParticipants() {
        long eventId = 1;
        List<User> eventUsers = new ArrayList<>();
        User user1 = User.builder()
                .id(123)
                .build();
        User user2 = User.builder()
                .id(456)
                .build();
        eventUsers.add(user1);
        eventUsers.add(user2);

        when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(eventUsers);

        List<User> actualParticipants = eventParticipationService.getParticipant(eventId);

        assertEquals(eventUsers, actualParticipants);
    }

    @Test
    void testGetParticipant_NonExistingEventId_ReturnsEmptyList() {
        long eventId = 999;

        when(eventParticipationRepository.existsById(eventId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.getParticipant(eventId);
        });
    }

    @Test
    void testGetParticipantsCount_ExistingEventId_ReturnsParticipantCount() {
        long eventId = 1;
        List<User> eventUsers = new ArrayList<>();
        eventUsers.add(new User());
        eventUsers.add(new User());
        eventUsers.add(new User());

        when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(eventUsers);

        int count = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(eventUsers.size(), count);
    }

    @Test
    void testGetParticipantsCount_NonExistingEventId_ThrowsIllegalArgumentException() {
        long eventId = 999;

        when(eventParticipationRepository.existsById(eventId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });
    }
}

