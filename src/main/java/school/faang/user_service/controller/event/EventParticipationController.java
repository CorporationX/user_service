package school.faang.user_service.controller.event;

import jakarta.xml.bind.ValidationException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.event.EventPartcipationService;

@RequestMapping("/Events")
@RestController
public class EventParticipationController {
    private EventPartcipationService eventParticipationService;

    @SneakyThrows
    public void validateId(long userId) {
        if(userId < 0){
            throw new ValidationException("incorrect data is specified");
        }
    }
    //@Autowired
    public EventParticipationController(EventPartcipationService eventParticipationService){
        this.eventParticipationService = eventParticipationService;
    }
    //@Bean

    @PostMapping("/registration")
    public void registerParticipant(long userId, long eventId){
        validateId(userId);
        validateId(eventId);
        eventParticipationService.registerParticipant(userId, eventId);
    }
    //@Bean
// добавить id пользователя
    @PutMapping("unregistration")
    public void unregisterParticipant(long userId, long eventId){
        validateId(userId);
        validateId(eventId);
        eventParticipationService.unregisterParticipant(userId, eventId);
    }
    //@Bean

    @GetMapping("/getList/{id}")
    public void getParticipant(@PathVariable long eventId){
        validateId(eventId);
        eventParticipationService.getParticipant(eventId);
        System.out.println(123);
    }
    //@Bean

    @GetMapping("/getCount")
    public void getParticipantsCount(long eventId){
        validateId(eventId);
        eventParticipationService.getParticipantsCount(eventId);
        System.out.println(1234);
    }
}
