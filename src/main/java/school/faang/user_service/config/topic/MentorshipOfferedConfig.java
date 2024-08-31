package school.faang.user_service.config.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class MentorshipOfferedConfig {
    @Value("${spring.data.redis.channels.mentorship_offered}")
    private String mentorshipOfferedChannel;

    @Bean(name = "mentorshipOfferedChannel")
    public ChannelTopic mentorshipOfferedChannel(){
        return new ChannelTopic(mentorshipOfferedChannel);
    }
}
