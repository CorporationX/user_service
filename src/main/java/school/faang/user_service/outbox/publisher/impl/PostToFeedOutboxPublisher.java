package school.faang.user_service.outbox.publisher.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.post.PostToFeedEvent;
import school.faang.user_service.messages.kafka.producers.PostToFeedProducer;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.publisher.OutboxEventPublisher;

@Component
@RequiredArgsConstructor
public class PostToFeedOutboxPublisher implements OutboxEventPublisher {

    private final PostToFeedProducer postToFeedProducer;
    private final ObjectMapper objectMapper;

    @Override
    public OutboxEventType getType() {
        return OutboxEventType.POST_TO_FEED;
    }

    @Override
    public void publish(String payload) {
        try {
            PostToFeedEvent event =
                    objectMapper.readValue(payload, PostToFeedEvent.class);

            postToFeedProducer.publish(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}