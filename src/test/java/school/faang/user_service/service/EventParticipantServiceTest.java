package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.Service.event.EventParticipationService;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipantServiceTest {

    @Test
    void registerPatricipant_UserRegistred_ThrowException() {
        EventParticipationService eventParticipationService = new EventParticipationService();
        assertThrows(RuntimeException.class , () -> eventParticipationService.registrParticipant(2 , 1));
    }

    @Test
    void unregisterParticipant_UserNotRegistred_ThrowException(){
        EventParticipationService eventParticipationService = new EventParticipationService();
        assertThrows(RuntimeException.class , () -> eventParticipationService.unregisterParticipant(2,1));
    }

    @Test
    void getParticipants_ReturnListOfParticipants(){
        EventParticipationService eventParticipationService = new EventParticipationService();
        assertEquals(2, eventParticipationService.getPaticipant(1).size());
    }

    @Test
    void getParticipantsCount_ReturnParticipantsCount(){
        EventParticipationService eventParticipationService = new EventParticipationService();
        assertEquals(2, eventParticipationService.getParticipantCount(1));
    }
}
