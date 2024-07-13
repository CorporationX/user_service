package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EventController {

    private final EventService eventService;

    public EventDto create(EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    public void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Заголовок события не должен быть пустым");
        }
        if (event.getStartDate() == null) {
            throw new IllegalArgumentException("У события должна быть дата начала");
        }
        if (event.getOwnerId() == null) {
            throw new IllegalArgumentException("У события должен быть пользователь, который его запускает");
        }
        if (event.getStartDate() == null) {
            throw new IllegalArgumentException("У события должна быть дата начала");
        }
        if (event.getRelatedSkills() == null) {
            throw new IllegalArgumentException("Событие должно иметь связанные скилы");
        }
    }

    public EventDto getEventById(Long eventId){
        return eventService.getEventById(eventId);
    }

    private List<EventDto> getEventsByFilter(EventFilterDto filters){
        return eventService.getEventsByFilter(filters);
    }

    public void deleteEvent(Long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(Long eventId, EventDto event) {
        validateEvent(event);
        return eventService.updateEvent(eventId, event);
    }

    public List<EventDto>  getOwnedEvents(Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto>  getParticipatedEvents(Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
