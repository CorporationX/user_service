package school.faang.user_service.config.context.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.student.DtoBanSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            userService.banUsers(objectMapper.readValue(message.getBody(), DtoBanSchema.class).getIds());
        } catch (IOException e) {
            log.error("Failed to deserialize DtoBanSchema to JSON", e);
        }
    }
}
