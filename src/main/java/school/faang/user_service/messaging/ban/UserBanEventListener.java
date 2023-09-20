package school.faang.user_service.messaging.ban;



import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.userban.UserBanEventWorker;
import school.faang.user_service.util.Mapper;

@Component
@RequiredArgsConstructor
public class UserBanEventListener implements MessageListener {
    private final Mapper jsonMapper;

    private final UserBanEventWorker userBanEventWorker;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        jsonMapper.toJson(message.toString())
                .ifPresent(s -> userBanEventWorker.saveUserBanEvent(s));
    }
}
