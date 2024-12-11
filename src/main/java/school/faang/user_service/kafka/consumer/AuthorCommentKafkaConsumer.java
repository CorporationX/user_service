package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.event.kafka.AuthorCommentKafkaEvent;
import school.faang.user_service.service.RedisUserService;

@Service
public class AuthorCommentKafkaConsumer extends AbstractKafkaConsumer<AuthorCommentKafkaEvent> {
    private final RedisUserService redisUserService;

    public AuthorCommentKafkaConsumer(ObjectMapper objectMapper, RedisUserService redisUserService) {
        super(objectMapper, AuthorCommentKafkaEvent.class);
        this.redisUserService = redisUserService;
    }

    @Override
    protected void processEvent(AuthorCommentKafkaEvent event) {
        redisUserService.saveUser(event.getCommentAuthorId());
    }

    @KafkaListener(
            topics = "${kafka.topics.author-of-comment}",
            groupId = "${kafka.consumer.groups.user-service.group-id}",
            concurrency = "${kafka.consumer.groups.user-service.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format(
                "Failed to deserialize user made comment event: %s and add him to user map in redis", eventJson), e);
    }
}
