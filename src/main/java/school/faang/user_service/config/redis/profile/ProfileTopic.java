package school.faang.user_service.config.redis.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class ProfileTopic {

    @Bean
    ChannelTopic profileViewTopic(@Value("${spring.data.channel.profile_view.name}") String topicName) {
        return new ChannelTopic(topicName);
    }
}
