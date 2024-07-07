package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.Service.event.EventParticipationService;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipantServiceTest {

    @BeforeEach
    public void setUp(){
        EventParticipationService eventParticipationService = new EventParticipationService();
    }

    @Test
    void registerPatricipant_UserRegistred_ThrowException() {
        assertThrows(RuntimeException.class , () -> eventParticipationService.registrParticipant(2 , 1));
    }

    @Test
    void unregisterParticipant_UserNotRegistred_ThrowException(){

    }
}
