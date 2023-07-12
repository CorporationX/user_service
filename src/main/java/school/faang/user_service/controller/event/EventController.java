package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Component("eventController")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event){
        if (eventService.validation(event)) {
            return eventService.create(event);
        }else {
            throw new DataValidationException("Ошибка");
        }
    }
    public EventDto getEvent(long id){
        return eventService.create(eventService.getEvent(id));
    }
    public void getEventsByFilter(EventFilterDto filter){

        eventService.getEventsByFilter(filter);
    }
    public void deleteEvent(long id){
        eventService.deleteEvent(id);
    }
}
