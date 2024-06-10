package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;

import java.util.List;

public interface EventService {
    void deleteById(long eventId);
    EventDto findById(Long eventId);
    EventDto createEvent(EventDto eventDto);
    List<EventDto> findAll();
}