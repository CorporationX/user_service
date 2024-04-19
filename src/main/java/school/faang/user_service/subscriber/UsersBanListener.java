package school.faang.user_service.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.UserEvent;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersBanListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            UserEvent userEvent = objectMapper.readValue(message.getBody(), UserEvent.class);
            log.info(String.format("UserService getting userId %d who should be banned", userEvent.getUserId()));
            userService.banUser(userEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
