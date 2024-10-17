package school.faang.user_service.service.user.view.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;
import school.faang.user_service.service.publisher.AbstractEventAggregator;

import java.util.List;

@Slf4j
@Aspect
@EnableAspectJAutoProxy
@Component
@RequiredArgsConstructor
public class AspectRedisProfileViewEventPublisher extends AbstractEventAggregator<ProfileViewEventDto> {
    private static final String EVENT_TYPE_NAME = "Profile view";

    private final RedisTemplate<String, ProfileViewEventDto> profileViewEventDtoRedisTemplate;
    private final ChannelTopic profileViewEventTopic;
    private final UserContext userContext;

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
            throw new InvalidReturnTypeException("Method annotated with @SendUserViewAnalyticEvent must return User or List<User>");
        }
    }

    @Override
    protected void publishEvents(List<ProfileViewEventDto> eventsCopy) {
        profileViewEventDtoRedisTemplate.convertAndSend(profileViewEventTopic.getTopic(), eventsCopy);
        log.info("Publish into topic: {}, message: {}", profileViewEventTopic.getTopic(), eventsCopy);
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
