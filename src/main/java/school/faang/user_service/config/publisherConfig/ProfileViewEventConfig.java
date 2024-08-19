package school.faang.user_service.config.publisherConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class ProfileViewEventConfig {

    @Value("${spring.data.redis.channels.profileView.name}")
    private String ProfileViewEventChannel;

    @Bean
    public ChannelTopic profileViewChannelTopic() {
        return new ChannelTopic(ProfileViewEventChannel);
    }
}
