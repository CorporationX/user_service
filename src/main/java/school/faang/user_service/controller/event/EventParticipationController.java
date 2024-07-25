package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/event-participation")
@Tag(name = "Event Participation API", description = "API for managing event participation")
public class EventParticipationController {

    @Autowired
    private EventParticipationService eventParticipationService;

    @PostMapping("/register")
    @Operation(summary = "Register a participant", description = "Registers a user as a participant in an event")
    public void registerParticipant(@RequestParam @Parameter(description = "ID of the user") long userId,
                                    @RequestParam @Parameter(description = "ID of the event") long eventId) {
        eventParticipationService.registrParticipant(userId, eventId);
    }

    @DeleteMapping("/unregister")
    @Operation(summary = "Unregister a participant", description = "Unregisters a user from an event")
    public void unregisterParticipant(@RequestParam @Parameter(description = "ID of the user") long userId,
                                      @RequestParam @Parameter(description = "ID of the event") long eventId) {
        eventParticipationService.unregisterParticipant(userId, eventId);
    }

    @GetMapping("/participants")
    @Operation(summary = "Get participants", description = "Retrieves the list of participants for an event")
    public List<UserDto> getParticipant(@RequestParam @Parameter(description = "ID of the event") long eventId) {
        return eventParticipationService.getPaticipant(eventId);
    }

    @GetMapping("/participant-count")
    @Operation(summary = "Get participant count", description = "Retrieves the count of participants for an event")
    public int getParticipantCount(@RequestParam @Parameter(description = "ID of the event") long eventId) {
        return eventParticipationService.getParticipantCount(eventId);
    }

}
