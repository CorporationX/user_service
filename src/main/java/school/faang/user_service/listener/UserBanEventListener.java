package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.application.UserIdsReceivedEvent;
import school.faang.user_service.event.redis.BanedUserEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Slf4j
@Component
public class UserBanEventListener extends AbstractEventListener<BanedUserEvent> implements MessageListener {

    private static final int THREE_HOURS_IN_MILLISECONDS = 3 * 60 * 60 * 1000;
    private static final int MAX_ELEMENTS_TO_DRAIN = 100;

    private final List<Long> idQueue;
    private final ApplicationEventPublisher eventPublisher;

    public UserBanEventListener(ObjectMapper objectMapper,
                                ApplicationEventPublisher eventPublisher) {
        super(objectMapper);
        this.idQueue = new ArrayList<>();
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, BanedUserEvent.class, event -> {
            try {
                Long userId = event.getUserId();
                idQueue.add(userId);
                log.debug("Baned user id = {} from Redis was successfully added to the queue", userId);

                if (idQueue.size() >= MAX_ELEMENTS_TO_DRAIN) {
                    drainAndPublish();
                }
            } catch (NumberFormatException e) {
                log.error("The message {} from Redis cannot be converted to userId",
                        new String(message.getBody(), StandardCharsets.UTF_8), e);
            }
        });
    }

    private void drainAndPublish() {
        List<Long> idsToProcess = new ArrayList<>(idQueue);
        idQueue.clear();

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