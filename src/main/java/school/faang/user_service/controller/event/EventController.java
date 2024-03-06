package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public EventDto create(@RequestBody EventDto event) {
        //ResponseEntity<EventDto>
        validateEvent(event);
        //EventDto createdEvent = eventService.create(event);
        return eventService.createEvent(event);
        //new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    private void validateEvent(EventDto event) {
        if(StringUtils.isEmpty(event.getTitle())){
            throw new DataValidationException("The title of the event can't be empty");
        }
        if(event.getStartDate()==null){
            throw new DataValidationException("The date of start can't be null");
        }
        if(event.getOwnerId()==null){
            throw new DataValidationException("The ownerID can't be null ");
        }
//        if (StringUtils.isEmpty(event.getTitle()) || event.getStartDate() == null || event.getOwnerId() == null) {
//            throw new IllegalArgumentException(" Event title, StartDate and Ownerid required for validation ");
//
//        }
    }
}
