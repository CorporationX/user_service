package school.faang.user_service.config.redis.premium;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

@Configuration
public class PremiumBoughtEventPublisherRedisConfig {
    @Value("${app.premium-redis-config.premium_bought_event_topic}")
    private String premiumBoughtEvent;

    @Bean
    public ChannelTopic premiumBoughtEventTopic() {
        return new ChannelTopic(premiumBoughtEvent);
    }
}
