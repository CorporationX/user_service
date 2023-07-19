package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping("/event")
    public EventDto createEvent(@RequestBody EventDto eventDto) {
        eventValidator.validateEvent(eventDto);
        return eventService.createEvent(eventDto);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventValidator.idValidate(id);
        eventService.deleteEvent(id);
    }

    @PutMapping("/event/{id}")
    public EventDto updateEvent(@PathVariable Long id, @RequestBody EventDto eventDto) {
        eventValidator.idValidate(id);
        eventValidator.validateEvent(eventDto);
        return eventService.updateEvent(id, eventDto);
    }

    @PostMapping("/event/list")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }
  
    @GetMapping("/event/{userId}/owned")
    public List<EventDto> getOwnedEvents(@PathVariable Long userId) {
        eventValidator.idValidate(userId);
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/event/{userId}/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable Long userId) {
        eventValidator.idValidate(userId);
        return eventService.getParticipatedEvents(userId);
    }
  
    @GetMapping("/event/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        eventValidator.idValidate(id);
        return eventService.getEvent(id);
    }

}
