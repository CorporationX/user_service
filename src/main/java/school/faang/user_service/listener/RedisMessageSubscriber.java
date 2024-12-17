package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserIdDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            UserIdDto user = objectMapper.readValue(message.getBody(), UserIdDto.class);
            userService.banUser(user.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
