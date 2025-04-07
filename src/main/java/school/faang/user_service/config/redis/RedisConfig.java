package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private final ObjectMapper objectMapper;
    private final RedisTopics redisTopics;

    public RedisConfig(ObjectMapper objectMapper, RedisTopics redisTopics) {
        this.objectMapper = objectMapper;
        this.redisTopics = redisTopics;
    }

    @Bean
    public List<ChannelTopic> topics() {
        List<String> topicList = redisTopics.getTopics();
        return topicList.stream()
                .map(ChannelTopic::new)
                .toList();
    }

    @Bean
    public Map<String, ChannelTopic> topicMap(List<ChannelTopic> topics) {
        return topics.stream()
                .collect(Collectors.toMap(ChannelTopic::getTopic, Function.identity()));
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        return  new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Object.class));
        return template;
    }
}

