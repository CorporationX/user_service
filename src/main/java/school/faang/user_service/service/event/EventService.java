package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ExecutorService executorService;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    public void removePastEvents() {
        List<Long> expiredEventIds = eventRepository.findExpiredEventIds(LocalDateTime.now());

        if (expiredEventIds.isEmpty()) {
            log.info("No expired events found");
            return;
        }

        log.info("Found {} expired events to delete", expiredEventIds.size());

        List<List<Long>> batches = partitionIntoBatches(expiredEventIds, batchSize);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(
                        () -> {
                            try {
                                eventRepository.deleteAllById(batch);
                                log.debug("Successfully deleted batch of {} events", batch.size());
                            } catch (Exception e) {
                                log.error("Failed to delete batch. Batch size: {}, First ID: {}, Last ID: {}",
                                        batch.size(),
                                        batch.get(0),
                                        batch.get(batch.size() - 1),
                                        e);
                            }
                        },
                        executorService
                ))
                .toList();

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("Deletion process completed successfully");
        } catch (CompletionException e) {
            log.error("Failed to complete some batch deletions during parallel execution", e);
        }
    }

    private List<List<Long>> partitionIntoBatches(List<Long> ids, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += batchSize) {
            batches.add(ids.subList(i, Math.min(i + batchSize, ids.size())));
        }
        return batches;
    }
}