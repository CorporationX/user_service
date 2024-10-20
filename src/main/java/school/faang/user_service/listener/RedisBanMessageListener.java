package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.UserBanEvent;
import school.faang.user_service.service.UserService;

import java.io.IOException;

@Component
@Slf4j
public class RedisBanMessageListener implements MessageListener {

    private final UserService userService;

    @Lazy
    public RedisBanMessageListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            UserBanEvent userBanEvent = new ObjectMapper().readValue(message.getBody(), UserBanEvent.class);
            userService.banUserById(userBanEvent.id());
        } catch (IOException e) {
            log.error("Received message parsing failed, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
