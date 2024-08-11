package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.ReadEvetDto;
import school.faang.user_service.dto.event.WriteEventDto;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    public ReadEvetDto createEvent(WriteEventDto writeEventDto) {
        return eventService.create(writeEventDto);
    }

    public List<ReadEvetDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.findAllByFilter(filter);
    }

    public ReadEvetDto getEvent(Long id) {
        return eventService.findById(id);
    }

    public void deleteEvent(Long id) {
        eventService.delete(id);
    }

    public ReadEvetDto updateEvent(Long id, WriteEventDto writeEventDto) {
        return eventService.update(id, writeEventDto);
    }

    public List<ReadEvetDto> getEventsByUserId(long userId) {
       return eventService.findAllByUserId(userId);
    }

    public List<ReadEvetDto> getParticipatedEvents(long userId) {
        return eventService.findParticipatedEventsByUserId(userId);
    }

}
