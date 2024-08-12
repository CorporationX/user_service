package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventparticipation")
@Validated
@Tag(name = "Event participation", description = "Event participation handler")
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @Operation(summary = "Register new participant")
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addParticipant(@RequestParam("eventId") @Min(value = 1L, message = "Event id cannot be less than 1") long eventId,
                               @RequestParam("userId") @Min(value = 1L, message = "User id cannot be less than 1") long userId) {
        eventParticipationService.addParticipant(eventId, userId);
    }

    @Operation(summary = "Unregister participant")
    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void removeParticipant(@RequestParam("eventId") @Min(value = 1L, message = "Event id cannot be less than 1") long eventId,
                                  @RequestParam("userId") @Min(value = 1L, message = "User id cannot be less than 1") long userId) {
        eventParticipationService.removeParticipant(eventId, userId);
    }

    @Operation(summary = "Get all event participants")
    @GetMapping("/participant")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getParticipant(@RequestParam("eventId") @Min(value = 1L, message = "Event id cannot be less than 1") long eventId) {
        return eventParticipationService.getParticipant(eventId);
    }

    @Operation(summary = "Get the number of event participants")
    @GetMapping("/participant/count")
    @ResponseStatus(HttpStatus.OK)
    public int getParticipantsCount(@RequestParam("eventId") @Min(value = 1L, message = "Event id cannot be less than 1") long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
