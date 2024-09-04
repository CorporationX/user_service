package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @PutMapping("/update")
    public EventWithSubscribersDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") Long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "ownerUsername", required = false) String ownerUsername) {

        return eventService.getEventsByFilter(new EventFilterDto(title, startDate, location, ownerUsername));
    }

    @GetMapping("/user/{userId}/owned")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/user/{userId}/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
