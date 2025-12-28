package school.faang.user_service.outbox.publisher;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.outbox.entity.OutboxEvent;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.entity.OutboxStatus;
import school.faang.user_service.outbox.repository.OutboxRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final List<OutboxEventPublisher> publishers;
    private Map<OutboxEventType, OutboxEventPublisher> publisherMap;

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${spring.outbox.page-size}")
    private int pageSize;

    @PostConstruct
    void init() {
        publisherMap = new EnumMap<>(OutboxEventType.class);
        for (OutboxEventPublisher publisher : publishers) {
            publisherMap.put(publisher.getType(), publisher);
        }
    }

    @Scheduled(fixedDelayString = "${spring.outbox.fixed-delay-ms}")
    public void publishPendingEvents() {
        int page = 0;
        Page<OutboxEvent> eventPage;

        do {
            Pageable pageable = PageRequest.of(page, pageSize);
            eventPage = outboxRepository.findByStatusAndSourceService(OutboxStatus.NEW, serviceName, pageable);

            List<OutboxEvent> events = eventPage.getContent();
            for (OutboxEvent event : events) {
                publishSingleEvent(event);
            }

            page++;
        } while (!eventPage.isEmpty());
    }

    @Transactional
    public void publishSingleEvent(OutboxEvent event) {
        try {
            OutboxEventPublisher publisher = publisherMap.get(event.getEventType());
            if (publisher == null) {
                throw new IllegalStateException(
                        "No OutboxEventPublisher for type " + event.getEventType()
                );
            }

            publisher.publish(event.getPayload());
            event.setStatus(OutboxStatus.SENT);
            outboxRepository.save(event);

        } catch (Exception e) {
            log.error("Failed to publish outbox event {}", event.getId(), e);
            event.setStatus(OutboxStatus.FAILED);
            outboxRepository.save(event);
        }
    }
}