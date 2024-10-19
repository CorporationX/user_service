package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.EventDto;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.service.EventService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserContext userContext;

    @PostMapping(value = "/filtered")
    public List<EventDto> getFilteredUsers(@RequestBody EventFilterDto filter) {
        long userId = userContext.getUserId();
        return eventService.getFilteredEvents(filter, userId);
    }

    @PostMapping()
    public EventDto createEvent(@RequestBody EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }
}
