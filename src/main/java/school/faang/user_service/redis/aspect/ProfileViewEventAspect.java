package school.faang.user_service.redis.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.redis.event.ProfileViewEvent;
import school.faang.user_service.redis.publisher.ProfileViewEventPublisher;

@Aspect
@Component
@RequiredArgsConstructor
public class ProfileViewEventAspect {
    private final ProfileViewEventPublisher publisher;
    private final UserContext userContext;

    @Async
    @AfterReturning(pointcut = "@annotation(PublishProfileViewEvent)")
    public void publishProfileViewEvent(JoinPoint joinPoint) {
        long requesterId = userContext.getUserId();
        long requestedId = (Long)joinPoint.getArgs()[0];
        ProfileViewEvent event = new ProfileViewEvent(requesterId, requestedId);
        publisher.publish(event);
    }
}
