package school.faang.user_service.redis.publisher.subscribe;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

    @Pointcut("@annotation(PublishSubscribeUserEvent)")
    public void publishEventPointcut() {
    }

    @Around("publishEventPointcut() && args(followerId, followeeId)")
    public Object publishEvent(ProceedingJoinPoint joinPoint, long followerId, long followeeId) throws Throwable {
        Object result = joinPoint.proceed();

        User follower = userRepository.getReferenceById(followerId);
        followEventPublisher.publish(new FollowerEvent(followerId, followeeId, follower.getUsername()));

        return result;
    }
}
