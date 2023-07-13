package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplementationTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private EventParticipationServiceImplementation eventParticipationServiceImplementation;

    @BeforeEach
    private void setUp() {
        eventParticipationServiceImplementation =
                new EventParticipationServiceImplementation(eventParticipationRepository);
    }

    @Test
    void testRegisterParticipant() {
        long eventId = 1L;
        long userId = 1L;
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(null);
        eventParticipationServiceImplementation.registerParticipant(eventId, userId);
        Mockito.verify(eventParticipationRepository, Mockito.only()).register(eventId, userId);
    }
}