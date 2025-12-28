package school.faang.user_service.messages.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.post.PostCreatedEvent;
import school.faang.user_service.dto.post.PostToFeedEvent;
import school.faang.user_service.outbox.entity.OutboxEvent;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.entity.OutboxStatus;
import school.faang.user_service.outbox.repository.OutboxRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.post.PostCreatedBatchService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;
    private final UserRepository userRepository;
    private final PostCreatedBatchService batchService;
    @Value("${spring.application.name}")
    private String serviceName;

    private static final int BATCH_SIZE = 500;
    private static final int PAGE_SIZE = 5000;

    @KafkaListener(
            topics = "${kafka.topics.post-created:post.created}",
            groupId = "user-service-group",
            containerFactory = "postCreatedKafkaListenerFactory"
    )
    public void listen(PostCreatedEvent event, Acknowledgment ack) {

        log.info("Received PostCreatedEvent for postId={} by authorId={}", event.getId(), event.getAuthorId());

        boolean alreadyProcessed = outboxRepository.existsByAggregateIdAndEventType(
                event.getId(), OutboxEventType.POST_TO_FEED
        );

        if (alreadyProcessed) {
            log.info("PostCreatedEvent for postId={} already processed, skipping", event.getId());
            ack.acknowledge();
            return;
        }

        int page = 0;
        List<Long> subscriberPage;

        try {
            do {
                Pageable pageable = PageRequest.of(page, PAGE_SIZE);
                subscriberPage = userRepository.findFollowerIdsPaged(event.getAuthorId(), pageable);

                for (List<Long> batch : partition(subscriberPage, BATCH_SIZE)) {
                    batchService.processBatch(event, batch);
                }

                page++;
            } while (!subscriberPage.isEmpty());

            ack.acknowledge();
            log.info("Finished processing PostCreatedEvent for postId={}", event.getId());
        } catch (Exception e) {
            log.error("Error processing PostCreatedEvent: {}", event, e);
        }
    }

    @Transactional
    public void processBatch(PostCreatedEvent event, List<Long> subscriberBatch) throws JsonProcessingException {
        try {
            PostToFeedEvent feedEvent = PostToFeedEvent.builder()
                    .postId(event.getId())
                    .authorId(event.getAuthorId())
                    .subscriberIds(subscriberBatch)
                    .createdAt(event.getCreatedAt())
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventType(OutboxEventType.POST_TO_FEED)
                    .aggregateId(event.getId())
                    .payload(objectMapper.writeValueAsString(feedEvent))
                    .status(OutboxStatus.NEW)
                    .sourceService(serviceName)
                    .build();

            outboxRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to save outbox batch for postId={}, subscribers={}", event.getId(), subscriberBatch, e);
            throw e;
        }
    }

    private static <T> List<List<T>> partition(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
}