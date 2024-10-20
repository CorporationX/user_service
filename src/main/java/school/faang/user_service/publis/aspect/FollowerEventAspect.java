package school.faang.user_service.publis.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.publis.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.UserRepository;

@Aspect
@Component
public class FollowerEventAspect {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowerEventPublisher followerEventPublisher;

    @Around("@annotation(PublisherFollowerEvent)")
    public Object publisherFollowerEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        long followerId = (Long) args[0];
        long followeeId = (Long) args[1];

        Object result = joinPoint.proceed();

        User follower = userRepository.findById(followerId).orElseThrow();
        followerEventPublisher.publishFollower(follower, followeeId);

        return result;
    }

}
