package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    long eventId;
    long registerId;
    long unregisteredId;
    User registerUser;
    User unregisteredUser;
    List<User> listUsersAtEvent;

    @BeforeEach
    void setup() {
        eventId = 1;
        registerId = 1;
        unregisteredId = 3;

        listUsersAtEvent = new ArrayList<>();
        registerUser = User.builder()
                .id(1)
                .build();
        User user2 = User.builder()
                .id(2)
                .build();
        unregisteredUser = User.builder()
                .id(3)
                .build();
        listUsersAtEvent.add(registerUser);
        listUsersAtEvent.add(user2);
    }
    @Test
    public void testRegisterParticipant_UserNotRegistered() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findById(unregisteredId)).thenReturn(Optional.of(unregisteredUser));
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        assertDoesNotThrow(() -> eventParticipationService.registerParticipant(eventId, unregisteredId));
    }

    @Test
    public void testRegisterParticipant_EventNotFound() {
        when(eventRepository.existsById(eventId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, registerId));
    }

    @Test
    public void testRegisterParticipant_UserAlreadyRegistered() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findById(registerId)).thenReturn(Optional.of(registerUser));
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.registerParticipant(eventId, registerId));
    }

    @Test
    void testUnregisterParticipant_UserRegistered_SuccessfullyUnregistered() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        assertDoesNotThrow(() -> eventParticipationService.unregisterParticipant(eventId, registerId));
        verify(eventParticipationRepository, times(1)).unregister(eventId, registerId);
    }

    @Test
    void testUnregisterParticipant_UserNotRegistered_ThrowsException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        assertThrows(IllegalArgumentException.class, () -> eventParticipationService.unregisterParticipant(eventId, unregisteredId));
        verify(eventParticipationRepository, never()).unregister(eventId, unregisteredId);
    }


    @Test
    void testGetParticipant_ExistingEventId_ReturnsListOfParticipants() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        List<User> actualParticipants = eventParticipationService.getParticipant(eventId);
        assertEquals(listUsersAtEvent, actualParticipants);
    }

    @Test
    void testGetParticipant_NonExistingEventId_ReturnsEmptyList() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.getParticipant(eventId);
        });
    }

    @Test
    void testGetParticipantsCount_ExistingEventId_ReturnsParticipantCount() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(listUsersAtEvent);
        int count = eventParticipationService.getParticipantsCount(eventId);
        assertEquals(listUsersAtEvent.size(), count);
    }

    @Test
    void testGetParticipantsCount_NonExistingEventId_ThrowsIllegalArgumentException() {
        when(eventParticipationRepository.existsById(eventId)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> {
            eventParticipationService.getParticipantsCount(eventId);
        });
    }
}

