package school.faang.user_service.service.event;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import school.faang.user_service.repository.event.EventRepository;

import javax.xml.stream.EventFilter;
import java.util.List;
import java.util.Optional;

@Component
@Data
public class EventService {
    private final EventDto eventDto;
    private EventRepository eventRepository;

    //вроде норм, но проверить
    public EventDto create(EventDto event) {
        Event event1 = new Event();
        event1.setId(event.getId());
        eventRepository.save(event1);
        return event;
    }

    //проверить на опшинал
    public EventDto getEvent(long eventId) {
        try {
         Optional<Event> event = eventRepository.findById(eventId);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    //доделать
    public void getEventsByFilter(EventFilterDto filter) {
        List<Event> eventList = eventRepository.findAll();
    }

    //готово, но проверить
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    //доделать
    public void updateEvent(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        eventRepository.save(event);
    }

    //проверить
    public List<Event> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventList;
    }

    //проверить, похож на предыдущий, надо исправить что-то
    public List<Event> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events;
    }


    private EventDto validate(EventDto event) {
        try {
            if (event.getRelatedSkills().isEmpty()) {
                return event;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Вы не можете провести событие с такими навыками");
        }
        return null;
    }
}
