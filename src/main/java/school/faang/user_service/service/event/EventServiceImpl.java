package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<Event> getPastEvents() {
        return eventRepository.findAllByEndDateBefore(LocalDateTime.now());
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new EntityNotFoundException("Event not found with id : %s".formatted(eventId)));
        log.debug("Event with id {} not found", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Async("taskExecutor")
    public void deleteEventsByIds(List<Long> ids) {
        eventRepository.deleteAllById(ids);
        log.info("Deleted {} past events with ids: {}", ids.size(), ids);
    }
}