package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.redis.user.RedisProfileViewEventPublisher;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileViewService {
    private final UserContext userContext;
    private final RedisProfileViewEventPublisher redisProfileViewEventPublisher;

    public void publish(long actorId) {
        long receiverId = userContext.getUserId();
        redisProfileViewEventPublisher.publish(new ProfileViewEventDto(receiverId, actorId));
    }

    public void publish(List<User> actors) {
        long receiverId = userContext.getUserId();
        actors.forEach(actor ->
                redisProfileViewEventPublisher.publish(new ProfileViewEventDto(receiverId, actor.getId())));
    }
}
