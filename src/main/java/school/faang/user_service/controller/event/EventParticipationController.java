package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController("/api/v1")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/event")
    public void registerParticipant(@RequestParam long eventId,
                                    @RequestParam long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    public int getParticipantCount(long eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }
}