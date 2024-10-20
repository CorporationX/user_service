package school.faang.user_service.controller.event;

import jakarta.xml.bind.ValidationException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.event.EventParticipationService;

// @Bean
@Controller
@RequestMapping("/event")
public class EventParticipationController {
    public EventParticipationService eventParticipationService;

    @PostMapping("/registration/{eventId}")
    public void register(@PathVariable long eventId, @RequestParam long userId){
        validateIds(userId, eventId);
        eventParticipationService.registerParticipant(userId, eventId);
    }

    @PostMapping("/unregistration/{eventId}")
    public void unregister(@PathVariable long eventId, @RequestParam long userId){
        validateIds(userId, eventId);
        eventParticipationService.unregisterParticipant(userId, eventId);
    }

    @GetMapping("/listParticipations/{eventId}")
    public void getPartipitions(@PathVariable long eventId){
        validateIds(eventId);
        eventParticipationService.getParticipant(eventId);
    }


    @GetMapping("/countPartipation/{eventId}")
    public void getCountParticipation(@PathVariable long eventId){
        validateIds(eventId);
        eventParticipationService.getParticipantsCount(eventId);
    }

    @SneakyThrows
    private void validateIds(long id){
        if(id < 0){
            throw new ValidationException("Id must be greater then zero");
        }
    }
    @SneakyThrows
    private void validateIds(long id1, long id2){
        if(id1 < 0 || id2 < 0){
            throw  new ValidationException("Id must be greater then zero");
        }
    }
}