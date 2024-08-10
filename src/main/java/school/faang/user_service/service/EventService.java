package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ThreadPoolTaskExecutor threadPool;
    @Value("${thread.pool.coreSize}")
    private final int quantityThreadPollSize;

    @Transactional
    public void deletingAllPastEvents() {
        List<Event> allEvents = eventRepository.findByStatus(EventStatus.COMPLETED);
        int sizeFullListEvent = allEvents.size();
        int baseSize = sizeFullListEvent / quantityThreadPollSize;
        int remainder = sizeFullListEvent % quantityThreadPollSize;
        int startIndex = 0;
        for (int i = 0; i < quantityThreadPollSize; i++) {
            int currentSize = baseSize + (i < remainder ? 1 : 0);
            int endIndex = startIndex + currentSize;
            List<Event> partListEvent = allEvents.subList(startIndex, Math.min(endIndex, sizeFullListEvent));
            threadPool.submit(() -> {
                eventRepository.deleteAllInBatch(partListEvent);
            });
            startIndex = endIndex;
        }
    }
}
