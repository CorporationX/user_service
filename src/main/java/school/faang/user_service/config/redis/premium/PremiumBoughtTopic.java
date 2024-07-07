package school.faang.user_service.config.redis.premium;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class PremiumBoughtTopic {

    @Bean
    public ChannelTopic premiumBoughtChannel(@Value("${spring.data.channel.premium_bought.name}") String topicName) {
        return new ChannelTopic(topicName);
    }
}
