package school.faang.user_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.UserEvent;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BanUserListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
        try {
            UserEvent userEvent = objectMapper.readValue(message.getBody(), UserEvent.class);
            userService.banUser(userEvent.getUserId());
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize message", e);
        }
    }
}
