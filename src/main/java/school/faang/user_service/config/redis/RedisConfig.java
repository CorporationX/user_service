package school.faang.user_service.config.redis;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
@Setter
public class RedisConfig {

    private List<String> topics;
    private final Map<String, ChannelTopic> topicMap = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Bean
    public Map<String, ChannelTopic> channelTopics() {
        for (String topic : topics) {
            topicMap.put(topic, new ChannelTopic(topic));
        }
        return topicMap;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }
}
