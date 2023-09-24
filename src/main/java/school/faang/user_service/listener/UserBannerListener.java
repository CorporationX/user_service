package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

@Component
public class UserBannerListener extends AbstractListener<Long> {
    public UserBannerListener(ObjectMapper objectMapper, UserService userService) {
        super(objectMapper, userService);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Long userId = readValue(message.getBody(), Long.class);
        send(userId);
    }
}