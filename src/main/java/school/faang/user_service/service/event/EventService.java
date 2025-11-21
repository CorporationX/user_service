package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ThreadPoolTaskExecutor executor;
    private final ChunkDeletionService chunkDeletionService;

    public EventService(
            EventRepository eventRepository,
            ThreadPoolTaskExecutor expiredEventTaskExecutor,
            ChunkDeletionService chunkDeletionService
    ) {
        this.eventRepository = eventRepository;
        this.executor = expiredEventTaskExecutor;
        this.chunkDeletionService = chunkDeletionService;
    }

    public void clearPastEvents(int chunkSize) {

        int pageNumber = 0;

        while (true) {
            Page<Long> page = eventRepository.findAllIdByStatus(
                    EventStatus.COMPLETED,
                    PageRequest.of(pageNumber, chunkSize)
            );

            if (page.isEmpty()) {
                log.info("No more completed events to delete.");
                break;
            }

            List<Long> idsToDelete = page.getContent();

            executor.execute(() -> {
                log.debug("Deleting {} events...", idsToDelete.size());
                chunkDeletionService.deleteChunk(idsToDelete);
            });

            pageNumber++;
        }
    }
}