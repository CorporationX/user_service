package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Component
public class EventValidator {

    public void validateEvents(List<Event> events){
        if(events == null){
            throw new DataValidationException("list of events is empty");
        }
    }
}
