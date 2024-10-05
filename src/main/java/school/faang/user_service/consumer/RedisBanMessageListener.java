package school.faang.user_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.BanEvent;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisBanMessageListener implements MessageListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            BanEvent banEvent = objectMapper.readValue(message.getBody(), BanEvent.class);
            log.info("Listener got userId {} who will be banned", banEvent.getId());
            userService.banUser(banEvent.getId());
        } catch (IOException e) {
            log.error("Error during parsing message from publisher: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
