package school.faang.user_service.controller.event;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import static school.faang.user_service.util.Utils.getErrMsgBindingRes;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    public EventDto create(@RequestBody @Valid EventDto eventDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new DataValidationException(getErrMsgBindingRes(bindingResult));
        return eventService.create(eventDto);
    }

}
