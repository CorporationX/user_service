package school.faang.user_service.config.redis.profilepic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class ProfilePicRedisConfig {

    @Value("${spring.data.redis.channels.profile_pic_channel.name}")
    private String profilePicChannelName;

    @Bean
    public ChannelTopic profilePicTopic() {
        return new ChannelTopic(profilePicChannelName);
    }
}
