package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.kafka.KafkaMessagePublisher;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfileViewEvent;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ProfileViewEventPublisher extends AbstractEventPublisher {
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannelName;
    private final UserContext userContext;

    @Autowired
    public ProfileViewEventPublisher(EventMapper eventMapper,
                                     ObjectMapper objectMapper,
                                     RedisMessagePublisher redisMessagePublisher,
                                     UserContext userContext,
                                     KafkaMessagePublisher kafkaMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher, kafkaMessagePublisher);
        this.userContext = userContext;
    }

    public void publishProfileViewEvent(Long profileViewedId) {
        userContext.setUserId(1L);
        redisMessagePublisher.publish(profileViewChannelName, new ProfileViewEvent(userContext.getUserId(),
                profileViewedId,
                LocalDateTime.now()));
    }

    public void publishProfileViewEventToKafka(Long profileViewedId) {
        kafkaMessagePublisher.publish(profileViewChannelName, new ProfileViewEvent(userContext.getUserId(),
                profileViewedId,
                LocalDateTime.now()));
    }
}
