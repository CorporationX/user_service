package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import school.faang.user_service.dto.event.participant.EventParticipationDto;
import school.faang.user_service.dto.event.participant.UserParticipationDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping("/register/{userId}/{eventId}")
    public void registerParticipant(@RequestBody UserParticipationDto userId,
                                    @RequestBody EventParticipationDto eventId) {
        notNull(userId, eventId);
        eventParticipationService.registerParticipation(eventId, userId);
    }

    @DeleteMapping("/unregister/{userId}/{eventId}")
    public void unregisterParticipant(@RequestBody UserParticipationDto userId,
                                      @RequestBody EventParticipationDto eventId) {
        notNull(userId, eventId);
        eventParticipationService.unregisterParticipation(eventId, userId);
    }

    @GetMapping("/participant/list/{eventId}")
    public List<UserParticipationDto> getParticipant(@RequestBody EventParticipationDto eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @GetMapping("/participant/{eventId}")
    public int getParticipantCount(@RequestBody EventParticipationDto eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }

    private void notNull(UserParticipationDto userId, EventParticipationDto eventId) {
        if (userId == null || eventId == null) {
            throw new IllegalArgumentException("userId and eventId cannot be null");
        }
    }
}
