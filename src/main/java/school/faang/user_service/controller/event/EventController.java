package school.faang.user_service.controller.event;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import static school.faang.user_service.util.Utils.getErrMsgBindingRes;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping
    public EventDto createEvent(@RequestBody @Valid EventDto eventDto, BindingResult bindingResult) {
        validateBindingResult(bindingResult);
        return eventService.createEvent(eventDto);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable @Positive(message = "id cannot be negative or equals 0") Long id) {
        return eventService.getEvent(id);
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new DataValidationException(getErrMsgBindingRes(bindingResult));
    }


}
