package school.faang.user_service.service.user.view.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;
import school.faang.user_service.redis.publisher.AbstractEventAggregator;

import java.util.List;

@Slf4j
@Aspect
@EnableAspectJAutoProxy
@Component
public class AspectRedisProfileViewEventPublisher extends AbstractEventAggregator<ProfileViewEventDto> {
    private static final String EVENT_TYPE_NAME = "Profile view";
    private final UserContext userContext;

    public AspectRedisProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                ObjectMapper javaTimeModuleObjectMapper,
                                                Topic profileViewEventTopic,
                                                UserContext userContext) {
        super(redisTemplate, javaTimeModuleObjectMapper, profileViewEventTopic);
        this.userContext = userContext;
    }

    @AfterReturning(
            pointcut = "(@annotation(school.faang.user_service.annotation.analytic.send.user.SendUserViewAnalyticEvent))",
            returning = "returnValue"
    )
    @SuppressWarnings("unchecked")
    public void addToPublish(Object returnValue) {
        if (returnValue instanceof User) {
            long receiverId = userContext.getUserId();
            long actorId = ((User) returnValue).getId();
            addToQueue(new ProfileViewEventDto(receiverId, actorId));
        } else if (returnValue instanceof List<?> users && !users.isEmpty() && users.get(0) instanceof User) {
            addToQueue(buildProfileViewEvents((List<User>) users));
        } else {
            throw new InvalidReturnTypeException("Method annotated with @SendUserViewAnalyticEvent must return User " +
                    "or List<User>");
        }
    }

    @Override
    protected String getEventTypeName() {
        return EVENT_TYPE_NAME;
    }

    private List<ProfileViewEventDto> buildProfileViewEvents(List<User> users) {
        long receiverId = userContext.getUserId();
        return users.stream()
                .map(user -> new ProfileViewEventDto(receiverId, user.getId()))
                .toList();
    }
}
