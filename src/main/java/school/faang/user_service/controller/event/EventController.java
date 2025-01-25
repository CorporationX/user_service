package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.CreateEventRequestDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.UpdateEventRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventResponseDto createEvent(@Valid @RequestBody CreateEventRequestDto createRequest) {
        return eventService.createEvent(createRequest);
    }

    @GetMapping("/{eventId}")
    public EventResponseDto getEvent(@PathVariable @Valid @Positive Long eventId) throws DataValidationException {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filter")
    public List<EventResponseDto> filterEvents(@Valid @RequestBody EventFilterDto filterDto) {
        return eventService.getEventsByFilters(filterDto);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable @Positive Long eventId) throws DataValidationException {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping("/{eventId}")
    public EventResponseDto updateEvent(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventRequestDto updateRequest) throws DataValidationException {
        updateRequest.setId(eventId);
        return eventService.updateEvent(updateRequest);
    }

    @GetMapping("/owner/{userId}")
    public List<EventResponseDto> getEventsByOwner(@PathVariable @Valid @Positive Long userId) {
        return eventService.getEventsByOwner(userId);
    }

    @GetMapping("/participant/{userId}")
    public List<EventResponseDto> getEventsByParticipant(@PathVariable @Valid @Positive Long userId) {
        return eventService.getEventsByParticipant(userId);
    }
}