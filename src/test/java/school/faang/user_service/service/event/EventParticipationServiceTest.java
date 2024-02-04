package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

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
}

