package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.repository.event.EventRepository;

@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;


    @Override
    public void removeEvent(long id) {
        eventRepository.deleteById(id);
    }
}
