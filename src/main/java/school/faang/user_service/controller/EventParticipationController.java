package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/eventParticipation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerParticipant(@RequestParam long userId, @RequestParam long eventId) {
        eventParticipationService.registerParticipant(userId, eventId);
    }

    @DeleteMapping("/unregister")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterParticipant(@RequestParam long userId, @RequestParam long eventId) {
        eventParticipationService.unregisterParticipant(userId, eventId);
    }

    @GetMapping("/participants")
    public List<UserDto> getParticipant(@RequestParam long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/count")
    public int getParticipantsCount(@RequestParam long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
