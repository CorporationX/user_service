package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    public EventReadDto create(EventCreateEditDto eventCreateEditDto) {
        return eventService.create(eventCreateEditDto);
    }

    public List<EventReadDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.findAll(filter);
    }

    public EventReadDto getEvent(Long id) {
        return eventService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void delete(Long id) {
        if (!eventService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public EventReadDto update(Long id, EventCreateEditDto editDto) {
        return eventService.update(id, editDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<EventReadDto> getOwnedEvents(long userId) {
       return eventService.findAllByUserId(userId);
    }

}
