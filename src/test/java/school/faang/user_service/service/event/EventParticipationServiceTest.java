package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.RegisterParticipantRequest;
import school.faang.user_service.repository.event.EventParticipationRepository;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {
    private final Long eventId = 1L;
    private final Long userId = 2L;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private RegisterParticipantRequest registerParticipantRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Captor
    private ArgumentCaptor<RegisterParticipantRequest> registerParticipantCaptor;

    @Test
    void eventValidationExist() {
    }

    @Test
    void userValidationExist() {
    }

    @Test
    void userNotFound() {
    }

    @Test
    void registerParticipant() {
    }

    @Test
    void unregisterParticipant() {
    }

    @Test
    void getParticipant() {
    }

    @Test
    void getParticipantCounts() {
    }
}