/*
package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final EventValidator validator;


    @Override
    public void removeEvents(List<Event> events) {
        validator.validateEvents(events);

        eventRepository.deleteAll(events);
    }
}
*/
