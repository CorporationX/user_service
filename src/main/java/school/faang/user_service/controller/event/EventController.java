package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserReadDto;
import school.faang.user_service.dto.event.ReadEvetDto;
import school.faang.user_service.dto.event.WriteEventDto;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadEvetDto createEvent(@RequestBody WriteEventDto writeEventDto) {
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

    @GetMapping("/{eventId}/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserReadDto> getUsersByEventId(@PathVariable Long eventId) {
        return userService.findAllByEventId(eventId);
    }
}
