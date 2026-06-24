package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserEventCountResDto;
import school.faang.user_service.dto.UserResDto;
import school.faang.user_service.event.EventParticipationService;

import java.util.List;

@RestController
@Valid
@RequestMapping("/events")
public class EventParticipationController {
    private static final String EVENT_ID = "/{eventId}";
    private static final String REGISTER_URL = EVENT_ID + "/register";
    private static final String UNREGISTER_URL = EVENT_ID + "/unregister";

    @Autowired
    private EventParticipationService eventParticipationService;

    @PostMapping(REGISTER_URL)
    public void registerParticipant(@PathVariable @NotNull Long eventId, @RequestParam @NotNull Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @DeleteMapping(UNREGISTER_URL)
    public void unregisterParticipant(@PathVariable long eventId, @RequestParam long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping(EVENT_ID)
    public List<UserResDto> getParticipant(@PathVariable long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping(EVENT_ID + "/count")
    public UserEventCountResDto getParticipantsCount(@PathVariable long eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }
}