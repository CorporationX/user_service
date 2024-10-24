package school.faang.user_service.redis.publisher.subscribe;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.redis.event.FollowerEvent;
import school.faang.user_service.redis.publisher.FollowEventPublisher;
import school.faang.user_service.repository.UserRepository;

@Aspect
@Component
public class SubscribeUserEventAspect {
    @Autowired
    private FollowEventPublisher followEventPublisher;

    @Autowired
    private UserRepository userRepository;

    @AfterReturning(pointcut = "@annotation(publishSubscribeUserEvent) && args(followerId, followeeId)")
    public void afterMethodWithEvent(long followerId, long followeeId, PublishSubscribeUserEvent publishSubscribeUserEvent) {
        User follower = userRepository.getReferenceById(followerId);
        followEventPublisher.publish(new FollowerEvent(followerId, followeeId, follower.getUsername()));
    }
}
