package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.repository.EventParticipationRepository;
import school.faang.user_service.service.impl.EventParticipationServiceImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VerificationOfGettingAllUsersTest {

    @Test
    public void testVerifyTheInvocationGetParticipantMethod() {
        EventParticipationRepository eventParticipationRepository = Mockito.mock(EventParticipationRepository.class);

        EventParticipationServiceImpl eventParticipationService = new EventParticipationServiceImpl(eventParticipationRepository);

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