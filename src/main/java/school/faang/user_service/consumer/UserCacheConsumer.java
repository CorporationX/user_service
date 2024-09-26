package school.faang.user_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.user.UserCacheEvent;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-cache", groupId = "user-cache-group")
    public void consume(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        UserCacheEvent userCacheEvent = objectMapper.readValue(message, UserCacheEvent.class);

        long userId = userCacheEvent.getUserId();
        log.info("user ID {} for cache have been received", userId);

        try {
            userService.cacheUser(userId);
            acknowledgment.acknowledge();
            log.info("Successfully processed user {} and acknowledged message", userId);
        } catch (Exception e) {
            log.error("Failed to process user {}: {}", userId, e.getMessage());
        }
    }
}
