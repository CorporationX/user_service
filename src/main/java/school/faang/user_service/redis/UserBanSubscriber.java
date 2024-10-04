package school.faang.user_service.redis;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.application_event.UserIdsReceivedEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Getter
@Component
public class UserBanSubscriber implements MessageListener {
    private static final int THREE_HOURS_IN_MILLISECONDS = 3 * 60 * 60 * 1000;
    private static final int MAX_ELEMENTS_TO_DRAIN = 100;

    private final BlockingQueue<Long> idQueue = new LinkedBlockingQueue<>();
    private final ApplicationEventPublisher eventPublisher;

    public UserBanSubscriber(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            Long userId = Long.parseLong(messageBody);
            idQueue.put(userId);
            log.debug("Message {} from Redis was successfully added to the queue", messageBody);

            if (idQueue.size() >= MAX_ELEMENTS_TO_DRAIN) {
                drainAndPublish();
            }
        } catch (NumberFormatException e) {
            log.error("The message {} from Redis cannot be converted to userId",
                    new String(message.getBody(), StandardCharsets.UTF_8), e);
        } catch (InterruptedException e) {
            log.error("InterruptedException was thrown in idQueue", e);
        }
    }

    private void drainAndPublish() {
        List<Long> idsToProcess = new ArrayList<>();
        idQueue.drainTo(idsToProcess, MAX_ELEMENTS_TO_DRAIN);

        if (!idsToProcess.isEmpty()) {
            eventPublisher.publishEvent(new UserIdsReceivedEvent(this, idsToProcess));
        }
    }

    @Scheduled(fixedDelay = THREE_HOURS_IN_MILLISECONDS)
    private void processRemainingIds() {
        if (!idQueue.isEmpty()) {
            drainAndPublish();
        }
    }
}
