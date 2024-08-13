package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.thread.ThreadPoolDistributor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ThreadPoolDistributor threadPoolDistributor;

    @Transactional
    public void deletingAllPastEvents() {
        List<Event> allEvents = eventRepository.findByStatus(EventStatus.COMPLETED);
        int quantityThreadPollSize = threadPoolDistributor.customThreadPool().getPoolSize();
        int sizeFullListEvent = allEvents.size();
        int baseSize = sizeFullListEvent / quantityThreadPollSize;
        int remainder = sizeFullListEvent % quantityThreadPollSize;
        int startIndex = 0;
        for (int i = 0; i < quantityThreadPollSize; i++) {
            int currentSize = baseSize + (i < remainder ? 1 : 0);
            int endIndex = startIndex + currentSize;
            List<Event> partListEvent = allEvents.subList(startIndex, Math.min(endIndex, sizeFullListEvent));
            threadPoolDistributor.customThreadPool().submit(() -> {
                eventRepository.deleteAllInBatch(partListEvent);
            });
            startIndex = endIndex;
        }
    }
}