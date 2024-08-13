package school.faang.user_service.config.publishers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class MentorshipConfig {
    @Value("${spring.data.redis.channels.mentorship_requested.name}")
    private String mentorshipRequestedChannel;

    @Bean
    public ChannelTopic mentorshipRequestedTopic() {
        return new ChannelTopic(mentorshipRequestedChannel);
    }
}