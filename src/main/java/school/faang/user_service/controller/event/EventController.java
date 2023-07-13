package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/event")
    public EventDto createEvent(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.createEvent(eventDto);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        idValidate(id);
        eventService.deleteEvent(id);
    }

    @PutMapping("/event/{id}")
    public EventDto updateEvent(@PathVariable Long id, @RequestBody EventDto eventDto) {
        idValidate(id);
        validateEvent(eventDto);
        return eventService.updateEvent(id, eventDto);
    }

    @PostMapping("/event/list")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }
    @GetMapping("/event/{userId}/owned")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        idValidate(userId);
        return eventService.getOwnedEvents(userId);
    }

    public void validateEvent(EventDto eventDto) {
        if(eventDto == null){
            throw new DataValidationException("Event cannot be null");
        }
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null");
        }
    }
    private void idValidate(long id) {
        if (id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }
}
