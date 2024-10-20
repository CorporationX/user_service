package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.util.Pair;
import school.faang.user_service.listener.ban.UserBanEventListener;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    ChannelTopic goalCompletedEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getGoalCompletedEvent().getName());
    }

    @Bean
    public ChannelTopic followerTopic() {
        return new ChannelTopic(redisProperties.getChannels().getFollowerEventChannel().getName());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(List<Pair<MessageListenerAdapter, ChannelTopic>> requesters,
                                                        JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        requesters.forEach(
                (requester) -> container.addMessageListener(requester.getFirst(), requester.getSecond())
        );

        return container;
    }

    @Bean
    public List<Pair<MessageListenerAdapter, ChannelTopic>> requesters(
            MessageListenerAdapter userBanMessageListener,
            ChannelTopic userBanTopic)
    {
        return List.of(
                Pair.of(userBanMessageListener, userBanTopic)
        );
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(redisProperties.getChannels().getUserBanChannel().getName());
    }

    @Bean
    public MessageListenerAdapter userBanMessageListener(UserBanEventListener userBanEventListener) {
        return new MessageListenerAdapter(userBanEventListener);
    }
}
