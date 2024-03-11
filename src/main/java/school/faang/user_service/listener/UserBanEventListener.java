package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserBanEventDto;
import school.faang.user_service.service.user.UserService;

@Component
@Slf4j
public class UserBanEventListener extends AbstractEventListener<UserBanEventDto> {
    private final UserService userService;

    public UserBanEventListener(UserService userService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.userService = userService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        processEvent(
                message,
                (event) -> userService.banUserById(event.getUserId()),
                UserBanEventDto.class
        );
    }
}
