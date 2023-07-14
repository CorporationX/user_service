package school.faang.user_service.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody EventDto eventDto) {
        try {
            System.out.println(eventDto);
            validateEvent(eventDto);
            return ResponseEntity.ok(eventService.create(eventDto));
        } catch (DataValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<?> getEvent(long eventId) {
        if (eventId < 0) {
            return ResponseEntity.badRequest().body("Event ID cannot be less than 0");
        }
        try {
            return ResponseEntity.ok(eventService.getEvent(eventId));
        } catch (NotFoundException e) {
            return ResponseEntity.ok().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<?>> getEventsByFilter(@RequestBody EventFilterDto filter) {
        try {
            List<EventDto> filteredEvents = eventService.getEventsByFilter(filter);
            return ResponseEntity.ok(filteredEvents);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (event.getOwnerId() == null || event.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null or negative");
        }
    }
}
