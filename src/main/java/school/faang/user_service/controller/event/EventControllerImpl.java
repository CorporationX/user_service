package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.dto.event.request.EventRequestDto;
import school.faang.user_service.dto.event.response.EventResponseDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/events")
public class EventControllerImpl {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<EventResponseDto> create(@RequestBody @Valid EventRequestDto request) {
        log.info("Получен запрос на создание события: {}", request);
        Event event = eventService.create(
                eventMapper.eventRequestToEventEntity(request),
                request.getRelatedSkills());
        EventResponseDto responseDto = eventMapper.eventToEventResponse(event);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@RequestBody @Valid EventRequestDto request,
                                                        @NotNull @Positive @PathVariable long id) {
        log.info("Получен запрос на обновление иваента: {}", request);
        Event updatedEvent = eventService.updateEvent(
                eventMapper.eventRequestToEventEntity(request),
                request.getRelatedSkills(),
                id);
        EventResponseDto responseDto = eventMapper.eventToEventResponse(updatedEvent);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEvent(@NotNull @Positive @PathVariable long id) {
        log.info("Получен запрос на получение события: {}", id);
        EventResponseDto responseDto = eventMapper.eventToEventResponse(eventService.getEvent(id));
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<EventResponseDto>> getEventsByFilter(@RequestBody @Valid EventFilterDto filter) {
        log.info("Получен запрос на поиск по фильтру: {}", filter);
        List<EventResponseDto> eventsResponse = eventMapper.toEventResponses(
                eventService.getEventsByFilter(filter));
        return ResponseEntity.ok(eventsResponse);
    }

    @GetMapping("/owned")
    public ResponseEntity<List<EventResponseDto>> getOwnedEvents() {
        List<EventResponseDto> eventsResponse = eventMapper.toEventResponses(eventService.getOwnedEvents());
        return ResponseEntity.ok(eventsResponse);
    }

    @GetMapping("/participated")
    public ResponseEntity<List<EventResponseDto>> getParticipatedEvents() {
        List<EventResponseDto> eventsResponse = eventMapper.toEventResponses(eventService.getParticipatedEvents());
        return ResponseEntity.ok(eventsResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@NotNull @Positive @PathVariable long id) {
        log.info("Получен запрос на удаление иваента с id: {}", id);
        String response = eventService.deleteEvent(id);
        return ResponseEntity.ok(response);
    }
}
