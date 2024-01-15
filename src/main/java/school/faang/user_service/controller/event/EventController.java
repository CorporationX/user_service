package school.faang.user_service.controller.event;


import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
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

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable @Positive(message = "id cannot be negative or equals 0") Long id) {
        return eventService.getEvent(id);
    }


    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable @Positive(message = "id cannot be negative or equals 0") Long id) {
        eventService.deleteEvent(id);
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


    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new DataValidationException(getErrMsgBindingRes(bindingResult));
    }


}
