package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.controller.utils.EventControllerUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.data.Required;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    private final EventControllerUtils eventControllerUtils;

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto event) {
        eventControllerUtils.isValidDateRange(event);

        return eventService.create(event);
    }

    @GetMapping(value = "/{id}")
    public EventDto getEvent(@PathVariable @Required Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping(value = "/filter/{id}")
    public List<EventDto> getEventsByFilter(@Valid @RequestBody EventFilterDto filter,
                                            @RequestParam(name = "page", defaultValue = "0")
                                            @Min(value = 0)
                                            Integer page,
                                            @RequestParam(name = "size", defaultValue = "10")
                                            @Min(value = 4) @Max(value = 10)
                                            Integer size,
                                            @PathVariable(name = "id") Long id) {
        eventControllerUtils.isValidDateRange(filter);
        return eventService.getEventsByFilter(filter, PageRequest.of(page, size), id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable @Required Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        eventControllerUtils.isValidDateRange(eventDto);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping(value = "/owned/{id}")
    public List<EventDto> getOwnedEvents(@PathVariable(value = "id") @NotNull @Positive Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping(value = "/participated/{id}")
    public List<EventDto> getParticipatedEvents(@PathVariable("id") @Required Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
