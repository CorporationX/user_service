package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.data.Required;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto event) {
        isValidDataRange(event);
        return eventService.create(event);
    }

    @GetMapping(value = "/{id}")
    public EventDto getEvent(@PathVariable @Required Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping(value = "/filter")
    public List<EventDto> getEventsByFilter(@Valid @RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable @Required Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        isValidDataRange(eventDto);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping(value = "/owned/{id}")
    public List<EventDto> getOwnedEvents(@PathVariable(value = "id", required = true) Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping(value = "/participated/{id}")
    public List<EventDto> getParticipatedEvents(@PathVariable("id") @Required Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    private void isValidDataRange(EventDto event) {
        if (event.getEndDate() != null) {
            if (event.getStartDate().isBefore(event.getEndDate())) {
                throw new DataValidationException("The end date must be after the start date");
            }
        }
    }
}
