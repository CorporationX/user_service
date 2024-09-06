package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventParticipationService;

@Component
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public ResponseEntity<Integer> getParticipantsCount(long eventId) {
        int count = eventParticipationService.getParticipantsCount(eventId);
        return ResponseEntity.ok(count);
    }
}