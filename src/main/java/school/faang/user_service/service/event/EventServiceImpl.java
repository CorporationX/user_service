package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${event.expiration}")
    private long expirationWeeks;

    @Override
    @Async("poolThreads")
    public void deleteExpiredEvents() {
        LocalDateTime overdueDate = LocalDateTime.now().minusWeeks(expirationWeeks);

        try {
            List<Event> allEvents = eventRepository.findAll()
                    .stream()
                    .filter(event -> event.getCreatedAt().isBefore(overdueDate))
                    .toList();

            if (!allEvents.isEmpty()) {
                eventRepository.deleteAll(allEvents);
                log.info("Deleted {} expired events.", allEvents.size());
            } else {
                log.info("No expired events.");
            }

        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
        }
    }
}
