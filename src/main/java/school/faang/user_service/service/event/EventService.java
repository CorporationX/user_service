package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.repository.event.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

//    public EventDto create(EventDto event) {
//        event.getOwnerId()
//        if (event.getOwnerId().)
//        return eventRepository.findBy(event.);
//    }
}
