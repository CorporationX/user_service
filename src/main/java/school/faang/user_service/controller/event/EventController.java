package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.validation.ValidationGroups;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;
import java.util.List;
import static school.faang.user_service.util.Utils.getErrMsgBindingRes;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@RequestBody @Validated(ValidationGroups.Create.class) EventDto eventDto, BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        return eventService.createEvent(eventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable @Positive(message = "id cannot be negative or equals 0") Long eventId) {
        return eventService.getEvent(eventId);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable @Positive(message = "id cannot be negative or equals 0") Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody @Validated(ValidationGroups.Update.class) EventDto eventDto, BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/owner/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable @Positive(message = "id cannot be negative or equals 0") Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participant/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable @Positive(message = "id cannot be negative or equals 0") long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @GetMapping("/filtered")
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new DataValidationException(getErrMsgBindingRes(bindingResult));
    }
}
