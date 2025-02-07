package school.faang.user_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.event.EventPostDto;
import school.faang.user_service.kafka.event.PostFeedDistributionEvent;
import school.faang.user_service.kafka.event.PostPublishedEvent;
import school.faang.user_service.kafka.producer.PostFeedDistributionEventProducer;
import school.faang.user_service.service.redis.UserCacheService;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostPublishedEventConsumer {

    private final UserCacheService userCacheService;
    private final SubscriptionService subscriptionService;
    private final PostFeedDistributionEventProducer feedDistributionProducer;
    private final ExecutorService feedDistributionExecutorService;

    @Value("${spring.kafka.event.feed-distribution.db-batch-size}")
    private int dbFollowersBatchSize;

    @Value("${spring.kafka.event.feed-distribution.batch-size}")
    private int eventFollowersBatchSize;

    @KafkaListener(topics = "${spring.kafka.topics.post-published}")
    public void listen(PostPublishedEvent event, Acknowledgment acknowledgment) {
        log.info("Processing PostPublishedEvent: {}", event);
        long authorId = event.getAuthorId();
        userCacheService.save(authorId);
        CompletableFuture<Void> sendingFeedEventsFuture = fetchFollowersAndSendEvents(authorId, event.getEventPostDtos());
        sendingFeedEventsFuture.thenRun(() -> {
                    log.info("Finished processing PostPublishedEvent: {}", event);
                    acknowledgment.acknowledge();
                }).exceptionally(ex -> {
                    log.error("Error while processing PostPublishedEvent: {}", event, ex);
                    return null;
                });
    }

    private CompletableFuture<Void> fetchFollowersAndSendEvents(long authorId, List<EventPostDto> postIds) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        long lastFollowerId = 0L;
        List<Long> followerIds;
        while (!(followerIds = subscriptionService.getActiveFollowersBatch(authorId, lastFollowerId, dbFollowersBatchSize)).isEmpty()) {
            List<Long> followerIdsCopy = new ArrayList<>(followerIds);
            futures.add(CompletableFuture.runAsync(() -> sendFeedDistributionEvents(followerIdsCopy, postIds), feedDistributionExecutorService));
            lastFollowerId = followerIds.get(followerIds.size() - 1);
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void sendFeedDistributionEvents(List<Long> followerIds, List<EventPostDto> eventPostDtos) {
        ListUtils.partition(followerIds, eventFollowersBatchSize)
                .forEach(followerIdsBatch -> feedDistributionProducer
                        .send(new PostFeedDistributionEvent(followerIdsBatch, eventPostDtos))
                );
    }
}