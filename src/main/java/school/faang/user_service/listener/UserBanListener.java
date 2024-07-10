package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBanListener implements MessageListener {

    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            Long userId = Long.valueOf(messageBody);

            log.info("Received message to ban user with ID: {}", userId);
            userService.banUser(userId);
        } catch (NumberFormatException e) {
            log.error("Failed to parse user ID from message: {}", new String(message.getBody()), e);
        }
    }
}