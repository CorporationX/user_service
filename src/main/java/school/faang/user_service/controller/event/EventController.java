package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.event.EventValidator;

import java.util.List;

@RequestMapping("/events")
@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping
    public EventDto createEvent(@RequestBody @Valid EventDto eventDto) {
        eventValidator.eventDatesValidation(eventDto);
        eventValidator.relatedSkillsValidation(eventDto);

        return eventService.createEvent(eventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable @Positive Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filters")
    public List<EventDto> getEventsByFilter(@RequestBody @NonNull EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilters(eventFilterDto);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable @Positive Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping
    public EventDto updateEvent(@RequestBody @Valid EventDto eventDto) {
        eventValidator.eventDatesValidation(eventDto);
        eventValidator.relatedSkillsValidation(eventDto);
        eventValidator.eventExistValidation(eventDto);

        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/owner/{userId}")
    public List<EventDto> getEventsOwner(@PathVariable @Positive Long userId) {
        return eventService.getEventsOwner(userId);
    }

    @GetMapping("/participant/{userId}")
    public List<EventDto> getEventParticipants(@PathVariable @Positive Long userId) {
        return eventService.getEventParticipants(userId);
    }
}
