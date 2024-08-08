package school.faang.user_service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.BanEvent;
import school.faang.user_service.service.UserBanedService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserBanedService userBanedService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            BanEvent banEvent = objectMapper.readValue(message.getBody(), BanEvent.class);
            userBanedService.banedUser(banEvent.getAuthorId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}