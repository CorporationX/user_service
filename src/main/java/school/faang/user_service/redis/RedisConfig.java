package school.faang.user_service.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import school.faang.user_service.service.user.UserService;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.channels.user-ban}")
    private String userBanTopic;

    @Value("${spring.data.redis.channels.event-start}")
    private String eventStartTopic;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    RedisMessageListenerContainer redisContainer(UserService userService) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListener(userService), userBanTopic());
        return container;
    }

    @Bean
    MessageListenerAdapter messageListener(UserService userService) {
        return new MessageListenerAdapter(new RedisMessageSubscriber(userService));
    }

    @Bean
    RedisMessagePublisher eventStartPublisher() {
        return new RedisMessagePublisher(redisTemplate(), eventStartTopic());
    }

    @Bean
    ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanTopic);
    }

    @Bean
    ChannelTopic eventStartTopic() {
        return new ChannelTopic(eventStartTopic);
    }
}