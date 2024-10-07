package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<Event> getPastEvents() {
        return eventRepository.findAllByEndDateBefore(LocalDateTime.now());
    }

    @Async("taskExecutor")
    public void deleteEventsByIds(List<Long> ids) {
        eventRepository.deleteAllById(ids);
        log.info("Deleted {} past events with ids: {}", ids.size(), ids);
    }
}
