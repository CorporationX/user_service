package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class MentorshipAcceptedTopicConfig {
    @Value("${spring.data.redis.channels.mentorship_accepted_channel.name}")
    private String mentorshipAcceptedChannelTopic;

    @Bean
    ChannelTopic mentorshipAcceptedChannel() {
        return new ChannelTopic(mentorshipAcceptedChannelTopic);
    }
}
