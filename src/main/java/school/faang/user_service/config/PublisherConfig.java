package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.publisher.skillOffer.RedisSkillOfferPublisher;
import school.faang.user_service.publisher.skillOffer.SkillOfferPublisher;

@Configuration
public class PublisherConfig {

    @Bean
    public SkillOfferPublisher skillOfferPublisher(
        RedisTemplate<String, Object> redisTemplate,
        ObjectMapper objectMapper,
        ChannelTopic skillOfferTopic
    ) {
        return new RedisSkillOfferPublisher(
            redisTemplate,
            objectMapper,
            skillOfferTopic
        );
    }
}
