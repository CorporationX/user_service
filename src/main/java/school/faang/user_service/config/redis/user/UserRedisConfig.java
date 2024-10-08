package school.faang.user_service.config.redis.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.user.ProfileViewEventDto;

@Configuration
public class UserRedisConfig {

    @Bean
    @Qualifier("profileViewEventTopic")
    public ChannelTopic profileViewEventTopic() {
        return new ChannelTopic("profileViewEvent");
    }

    @Bean
    public ProfileViewEventPublisher profileViewEventPublisher(RedisTemplate<String, ProfileViewEventDto> redisTemplate,
                                                               @Qualifier("profileViewEventTopic")
                                                               ChannelTopic topic) {
        return new RedisProfileViewEventPublisher(redisTemplate, topic);
    }
}
