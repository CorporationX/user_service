package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.producer.FeedHeatKafkaProducer;
import school.faang.user_service.model.event.kafka.AuthorPostByHeatKafkaEvent;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.RedisUserService;

@Service
public class AuthorPostByHeatKafkaConsumer extends AbstractKafkaConsumer<AuthorPostByHeatKafkaEvent> {
    @Value("${kafka.batch-size.follower:1000}")
    private int followerBatchSize;

    private final UserRepository userRepository;
    private final RedisUserService redisUserService;
    private final FeedHeatKafkaProducer feedHeatKafkaProducer;

    public AuthorPostByHeatKafkaConsumer(
            ObjectMapper objectMapper,
            UserRepository userRepository,
            RedisUserService redisUserService,
            FeedHeatKafkaProducer feedHeatKafkaProducer) {
        super(objectMapper, AuthorPostByHeatKafkaEvent.class);
        this.userRepository = userRepository;
        this.redisUserService = redisUserService;
        this.feedHeatKafkaProducer = feedHeatKafkaProducer;
    }

    @Override
    protected void processEvent(AuthorPostByHeatKafkaEvent event) {
        redisUserService.saveUser(event.getAuthorId());
        event.getLastCommentAuthors().forEach(redisUserService::saveUser);
        sendPostPublishedKafkaEvents(event);
    }

    @KafkaListener(
            topics = "${kafka.topics.author-post-by-heat}",
            groupId = "${kafka.consumer.groups.feed-heat.group-id}",
            concurrency = "${kafka.consumer.groups.feed-heat.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format(
                "Failed to deserialize user made post event: %s and add him to user map in redis", eventJson), e);
    }

    private void sendPostPublishedKafkaEvents(AuthorPostByHeatKafkaEvent event) {
        int pageNumber = 0;
        Page<Long> page;

        do {
            PageRequest pageRequest = PageRequest.of(pageNumber, followerBatchSize);
            page = userRepository.findUnbannedFollowerIdsByUserId(event.getAuthorId(), pageRequest);

            if (!page.hasContent()) {
                return;
            }

            PostPublishedKafkaEvent subEvent = new PostPublishedKafkaEvent(
                    event.getPostId(),
                    page.getContent(),
                    event.getPublishedAt());
            feedHeatKafkaProducer.sendEvent(subEvent);

            pageNumber++;
        } while (page.hasNext());
    }
}
