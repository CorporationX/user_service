package school.faang.user_service.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

@Component
@RequiredArgsConstructor
public class UserBannerListener implements MessageListener {
    private final UserService userService;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String messageBody = new String(message.getBody());
        System.out.println("Received message from channel '" + channel + "': " + messageBody);
        userService.banUser(Long.parseLong(messageBody));
    }
}