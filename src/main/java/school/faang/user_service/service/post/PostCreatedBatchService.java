package school.faang.user_service.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.post.PostCreatedEvent;
import school.faang.user_service.dto.post.PostToFeedEvent;
import school.faang.user_service.outbox.entity.OutboxEvent;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.entity.OutboxStatus;
import school.faang.user_service.outbox.repository.OutboxRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCreatedBatchService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    @Value("${spring.application.name}")
    private String serviceName;

    @Transactional
    public void processBatch(PostCreatedEvent event, List<Long> subscriberBatch)
            throws JsonProcessingException {
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
    }
}