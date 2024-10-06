package school.faang.user_service.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.BanEvent;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisBanMessageListener implements MessageListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            BanEvent banEvent = objectMapper.readValue(message.getBody(), new TypeReference<>() {});
            userService.banUserById(banEvent.getId());
        } catch (IOException e) {
            log.error("Received message parsing failed, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
