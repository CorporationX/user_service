package school.faang.user_service.aspect.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.aspect.EventPublisher;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.redis.publisher.AbstractEventAggregator;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
public class ProfileViewEventPublisherToRedis extends AbstractEventAggregator<ProfileViewEventDto>
        implements EventPublisher {
    private static final String EVENT_TYPE_NAME = "Profile view";
    private final UserContext userContext;
    private final UserRepository userRepository;

    public ProfileViewEventPublisherToRedis(RedisTemplate<String, Object> redisTemplate,
                                            ObjectMapper javaTimeModuleObjectMapper,
                                            Topic profileViewEventTopic,
                                            UserContext userContext,
                                            UserRepository userRepository) {
        super(redisTemplate, javaTimeModuleObjectMapper, profileViewEventTopic);
        this.userContext = userContext;
        this.userRepository = userRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publish(Object eventObject) {
        if (eventObject instanceof User user) {
            long receiverId = userContext.getUserId();
            String receiverName = userRepository.findById(userContext.getUserId()).orElseThrow().getUsername();

            addToQueue(new ProfileViewEventDto(receiverId, receiverName, user.getId(), user.getUsername()));

        } else if (eventObject instanceof List<?> users) {
            addToQueue(buildProfileViewEvents((List<User>) users));
        }
    }

    @Override
    protected String getEventTypeName() {
        return EVENT_TYPE_NAME;
    }

    @Override
    public Class<?> getInstance() {
        return User.class;
    }

    private List<ProfileViewEventDto> buildProfileViewEvents(List<User> users) {
        long receiverId = userContext.getUserId();
        String receiverName = userRepository.findById(userContext.getUserId()).orElseThrow().getUsername();

        return users.stream()
                .filter(user -> user.getId() != receiverId)
                .map(user -> new ProfileViewEventDto(receiverId, receiverName, user.getId(), user.getUsername()))
                .toList();
    }
}
