package school.faang.user_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.repository.OutboxEventRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final List<EventPublisher<?>> publishers;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    private void processOutboxEvents() {
        try {
            List<OutboxEvent> events = outboxEventRepository.findAllByProcessedFalse();

            for (OutboxEvent event : events) {
                try {
                    EventPublisher<?> sender = publishers.stream()
                            .filter(publisher -> publisher.getEventClass().getSimpleName().equals(event.getEventType()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("No publisher found for event type: "
                                    + event.getEventType()));

                    processEvent(sender, event);
                    event.setProcessed(true);
                    outboxEventRepository.save(event);
                } catch (Exception e) {
                    log.error("Error processing event: {}", event.getId(), e);
                }
            }
        } finally {
            isProcessing.set(false);
        }
    }

    @Scheduled(cron = "${cron.expressions.trigger-outbox-processing}")
    public void triggerProcessing() {
        if (isProcessing.compareAndSet(false, true)) {
            executorService.submit(this::processOutboxEvents);
        }
    }

    @Scheduled(cron = "${cron.expressions.clean-outbox}")
    public void cleanProcessedEvents() {
        log.info("Starting cleanup of processed events");
        int deletedCount = outboxEventRepository.deleteProcessedEvents();
        log.info("Cleanup completed. Deleted {} processed events", deletedCount);
    }

    @Transactional
    public void saveOutboxEvent(OutboxEvent outboxEvent) {
        outboxEventRepository.save(outboxEvent);
    }

    private <T> void processEvent(EventPublisher<T> publisher, OutboxEvent event) throws Exception {
        T payload = objectMapper.readValue(event.getPayload(), publisher.getEventClass());
        publisher.publish(payload);
    }
}