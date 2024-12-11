package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.producer.PostKafkaProducer;
import school.faang.user_service.model.event.kafka.AuthorPostKafkaEvent;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.RedisUserService;

@Service
public class AuthorPostKafkaConsumer extends AbstractKafkaConsumer<AuthorPostKafkaEvent> {
    @Value("${kafka.batch-size.follower:1000}")
    private int followerBatchSize;

    private final UserRepository userRepository;
    private final RedisUserService redisUserService;
    private final PostKafkaProducer postKafkaProducer;

    public AuthorPostKafkaConsumer(ObjectMapper objectMapper,
                                   UserRepository userRepository,
                                   RedisUserService redisUserService,
                                   PostKafkaProducer postKafkaProducer) {
        super(objectMapper, AuthorPostKafkaEvent.class);
        this.userRepository = userRepository;
        this.redisUserService = redisUserService;
        this.postKafkaProducer = postKafkaProducer;
    }

    @Override
    protected void processEvent(AuthorPostKafkaEvent event) {
        redisUserService.saveUser(event.getAuthorId());
        sendPostPublishedKafkaEvents(event);
    }

    @KafkaListener(
            topics = "${kafka.topics.author-published-post}",
            groupId = "${kafka.consumer.groups.user-service.group-id}",
            concurrency = "${kafka.consumer.groups.user-service.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format(
                "Failed to deserialize user made post event: %s and add him to user map in redis", eventJson), e);
    }

    private void sendPostPublishedKafkaEvents(AuthorPostKafkaEvent event) {
        int pageNumber = 0;
        Page<Long> page;

        do {
            PageRequest pageRequest = PageRequest.of(pageNumber, followerBatchSize);
            page = userRepository.findUnbannedFollowerIdsByUserId(event.getAuthorId(), pageRequest);

            if (page == null || !page.hasContent()) {
                return;
            }

            PostPublishedKafkaEvent subEvent = new PostPublishedKafkaEvent(
                    event.getPostId(),
                    page.getContent(),
                    event.getPublishedAt());
            postKafkaProducer.sendEvent(subEvent);

            pageNumber++;
        } while (page.hasNext());
    }
}
