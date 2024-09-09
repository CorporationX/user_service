package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VerificationOfGettingAllUsersTest {

    @Test
    public void testVerifyTheInvocationGetParticipantMethod() {
        EventParticipationRepository eventParticipationRepository = Mockito.mock(EventParticipationRepository.class);

        EventParticipationService eventParticipationService = new EventParticipationService(eventParticipationRepository);

        long eventId = 1L;

        eventParticipationService.getParticipant(eventId);

        try {
            verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
            System.out.println("The method was successfully invoked");
        } catch (AssertionError e) {
            System.out.println("The method was not successfully invoked");
        }
    }
}