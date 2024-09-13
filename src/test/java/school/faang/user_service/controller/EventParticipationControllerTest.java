package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.service.event.EventParticipationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {

    @InjectMocks
    EventParticipationController eventParticipationController;

    @Mock
    EventParticipationService eventParticipationService;

    private static long eventId;
    private static long userId;

    @BeforeAll
    static void setup() {
        eventId = 1L;
        userId = 1L;
    }

    @Test
    void registerParticipant_validRequest_serviceCalled() {
        eventParticipationController.registerParticipant(eventId, userId);
        verify(eventParticipationService, times(1)).registerParticipant(eventId, userId);
    }

    @Test
    void unregisterParticipant_validRequest_serviceCalled() {
        eventParticipationController.unregisterParticipant(eventId, userId);
        verify(eventParticipationService, times(1)).unregisterParticipant(eventId, userId);
    }

    @Test
    void getParticipant_validRequest_serviceCalled() {
        eventParticipationController.getParticipant(eventId);
        verify(eventParticipationService, times(1)).getParticipant(eventId);
    }

    @Test
    void getParticipantCount_validRequest_serviceCalled() {
        eventParticipationController.getParticipantCount(eventId);
        verify(eventParticipationService, times(1)).getParticipantCount(eventId);
    }
}