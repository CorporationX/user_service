package school.faang.user_service.service.eventService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public void deleteEventByUserId(long userId){
        List<Event> userEvents = eventRepository.findAllByUserId(userId);
        eventRepository.deleteAllById(userEvents.stream().map(Event::getId).toList());
    }
}
