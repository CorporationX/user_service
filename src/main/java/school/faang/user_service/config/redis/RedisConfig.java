package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.channels.recommendation-request-channel.name}")
    private String recommendationRequestTopic;

    @Bean
    public ChannelTopic recommendationRequestTopic() {
        return new ChannelTopic(recommendationRequestTopic);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RecommendationRequestedEventPublisher recommendationRequestedPublisher(
            RedisTemplate<String, Object> redisTemplate, ChannelTopic recommendationRequestTopic) {
        return new RecommendationRequestedEventPublisher(redisTemplate, recommendationRequestTopic);
    }

}
