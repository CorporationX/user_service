package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventAsyncService eventAsyncService;

    public boolean existsById(long id) {
        return eventRepository.existsById(id);
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Transactional
    public void clearEvents(int partitionSize) {
        List<Event> events = StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();

        List<List<Event>> partitions = ListUtils.partition(events, events.size() / partitionSize);
        partitions.forEach(eventAsyncService::clearEventsPartition);
    }
}
