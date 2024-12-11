package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.ParticipantDto;
import school.faang.user_service.dto.RegisterRequest;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerParticipant(@RequestBody @Valid RegisterRequest registerRequest) {
        eventParticipationService.registerParticipant(registerRequest.participantDto(), registerRequest.eventDto());

        return ResponseEntity.ok("User successfully registered for the event.");
    }

    @PostMapping("/unregister")
    public ResponseEntity<String> unregisterParticipant(@RequestBody @Valid RegisterRequest registerRequest) {
        eventParticipationService.unregisterParticipant(registerRequest.participantDto(), registerRequest.eventDto());

        return ResponseEntity.ok("User successfully unregistered from the event.");
    }

    @GetMapping("/participants/{eventId}")
    public ResponseEntity<List<ParticipantDto>> getParticipants(@PathVariable @Positive Long eventId) {
        List<ParticipantDto> participants = eventParticipationService.getParticipants(eventId);

        return ResponseEntity.ok(participants);
    }

    @GetMapping("/participants/count/{eventId}")
    public ResponseEntity<Integer> getParticipantsCount(@PathVariable @Positive Long eventId) {
        int count = eventParticipationService.getParticipantsCount(eventId);

        return ResponseEntity.ok(count);
    }
}
