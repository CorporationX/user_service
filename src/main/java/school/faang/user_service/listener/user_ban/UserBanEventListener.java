package school.faang.user_service.listener.user_ban;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.user_ban.UserBanEvent;
import school.faang.user_service.listener.AbstractEventListener;
import school.faang.user_service.service.user.UserService;

@Component
public class UserBanEventListener extends AbstractEventListener<UserBanEvent> {

    private final UserService userService;

    public UserBanEventListener(ObjectMapper objectMapper, UserService userService) {
        super(objectMapper);
        this.userService = userService;
    }

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        handleEvent(message, UserBanEvent.class, event -> {
            userService.banUserByIds(event.getUserIds());
        });
    }
}
