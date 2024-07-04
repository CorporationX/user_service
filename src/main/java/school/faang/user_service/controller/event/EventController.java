package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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

    public List<EventReadDto> getEventsByFilter(EventFilterDto filter){
        return eventService.findAll(filter);
    }
}
