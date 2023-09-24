package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.MessageReadException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractListener<T> implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    protected T readValue(byte[] json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            log.error("Failed to read message", e);
            throw new MessageReadException(e);
        }
    }

    @Transactional
    protected void send(T event) {
        userService.setUserBanner((Long) event);
        log.info("Send new event: {}", event);
    }
}